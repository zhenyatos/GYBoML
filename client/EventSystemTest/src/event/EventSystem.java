package event;

import javax.management.ObjectName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;


public class EventSystem {

    static {
        instanceEventSystem = new EventSystem();
    }

    private EventSystem() {
        logger = Logger.get();
        connections = new HashMap<>();
        methods = new HashMap<>();
    }

    public boolean connect(Object sender, String signal, Object receiver, String slot) {
        try {
            Method[] methodsSender = sender.getClass().getDeclaredMethods();
            Method[] methodsReceiver = receiver.getClass().getDeclaredMethods();

            Method methodSender = null, methodReceiver = null;

            for (Method mSender: methodsSender)
                if (mSender.getName().equals(signal)) {
                    Class[] signalParams = mSender.getParameterTypes();

                    for (Method mReceiver: methodsReceiver)
                        if (mReceiver.getName().equals(slot)) {

                            Class[] slotParams = mReceiver.getParameterTypes();
                            if (signalParams.length != slotParams.length) {
                                logger.registerLog(Logger.MsgType.INFO,
                                        "Parameters list mismatch. Signal:  " + signal + ", slot:" + slot);
                                return false;
                            }
                            for (int i = 0; i < signalParams.length; i++)
                                if (!signalParams[i].getCanonicalName().
                                        equals(slotParams[i].getCanonicalName())) {
                                    logger.registerLog(Logger.MsgType.INFO,
                                            "Parameters list mismatch. Signal:  " + signal + ", slot:" + slot);
                                    return false;
                                }
                            methodReceiver = mReceiver;
                            break;
                        }

                    methodSender = mSender;
                    break;
                }

            if (methodReceiver == null || methodSender == null)
                logger.registerLog(Logger.MsgType.INFO, "No such method: ");

            HashMap.SimpleEntry<Object, String>
                    key = new HashMap.SimpleEntry<>(sender, signal);

            HashSet<HashMap.SimpleEntry<Object, String>> receivers = new HashSet<>();

            if (connections.containsKey(key))
                receivers = connections.get(key);

            if (receivers != null)
                receivers.add(new HashMap.SimpleEntry<>(receiver, slot));
            else {
                logger.registerLog(Logger.MsgType.INFO, "Something went wrong in EventSystem::connect");
                return false;
            }

            connections.putIfAbsent(key, receivers);
            methods.put(new HashMap.SimpleEntry<>(receiver, slot), methodReceiver);
        }
        catch (NullPointerException exc) {
            logger.registerLog(Logger.MsgType.INFO, "No such method! " +  exc.getMessage());
            return false;
        }

        return true;
    }

    public void disconnect(Object sender, String signal, Object receiver, String slot) {
        HashMap.SimpleEntry<Object, String> key = new HashMap.SimpleEntry<>(sender, signal);
        HashSet<HashMap.SimpleEntry<Object, String>> receivers = connections.get(key);

        if (receivers == null)
            return;

        key = new HashMap.SimpleEntry<>(receiver, slot);
        receivers.remove(key);
        methods.remove(key);
    }

    public void emit(Object sender, String signal, Object ...params) {
        HashSet<HashMap.SimpleEntry<Object, String>>
                receivers = connections.get(new HashMap.SimpleEntry<>(sender, signal));

        if (receivers == null)
            return;

        try {
            theSender = sender;
            for (HashMap.SimpleEntry<Object, String> rec: receivers) {
                Method m = methods.get(rec);
                if (m == null)
                    logger.registerLog(Logger.MsgType.INFO, "This pair is not connected");
                else
                    m.invoke(rec.getKey(), params);
            }
        }
        catch (NullPointerException | IllegalArgumentException exc) {
            logger.registerLog(Logger.MsgType.INFO, "Bad argument list! " +  exc.getMessage());
        }
        catch (InvocationTargetException exc) {
            logger.registerLog(Logger.MsgType.INFO, "No such receiver! " +  exc.getMessage());
        }
        catch (IllegalAccessException exc) {
            logger.registerLog(Logger.MsgType.INFO, "Do not try to be hacker: no private method watch! " +  exc.getMessage());
        }
    }

    public static EventSystem get() {
        return instanceEventSystem;
    }

    public Object sender() {
        return theSender;
    }

    private HashMap<
                HashMap.SimpleEntry<Object, String>,
                HashSet<HashMap.SimpleEntry<Object, String>>> connections;
    private HashMap<HashMap.SimpleEntry<Object, String>, Method> methods;
    private Logger logger;

    private Object theSender;

    static private EventSystem instanceEventSystem;
}
