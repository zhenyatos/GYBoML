package main.java.ru.spbstu.gyboml.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;


public class EventSystem {

    static {
        instanceEventSystem = new EventSystem();
    }

    private EventSystem() {
        connections = new HashMap<>();
    }

    public boolean connect(Object sender, String signal, Object receiver, String slot) {
        HashMap.SimpleEntry<Object, String> key = new HashMap.SimpleEntry<>(sender, signal);

        HashSet<HashMap.SimpleEntry<Object, String>> receivers = connections.get(
                new HashMap.SimpleEntry<>(sender, signal));

        try {
            Class[]
                    methodSignal = sender.getClass().getMethod(signal).getParameterTypes(),
                    methodSlot = receiver.getClass().getMethod(slot).getParameterTypes();

            // check parameters compatibility
            if (methodSignal.length != methodSlot.length) {
                logger.registerLog(Logger.MsgType.INFO,"Parameters list mismatch. Signal:  " + signal + ", slot:" + slot);
                return false;
            }

            for (int i = 0; i < methodSignal.length; i++)
                if (!methodSignal[i].getCanonicalName().equals(methodSlot[i].getCanonicalName())) {
                    logger.registerLog(Logger.MsgType.INFO,"Parameters list mismatch. Signal:  " + signal + ", slot:" + slot);
                    return false;
                }

            if (receivers == null) {
                receivers = new HashSet<>();
                receivers.add(new HashMap.SimpleEntry<>(receiver, slot));
                connections.put(key, receivers);
            } else
                receivers.add(new HashMap.SimpleEntry<>(receiver, slot));
        }
        catch (NoSuchMethodException exc) {
            logger.registerLog(Logger.MsgType.INFO, "No such method! " +  exc.getMessage());
            return false;
        }

        return true;
    }

    public void disconnect(Object sender, String signal, Object receiver, String slot) {
        HashSet<HashMap.SimpleEntry<Object, String>> receivers = connections.get(
                new HashMap.SimpleEntry<>(sender, signal));

        if (receivers == null)
            return;

        receivers.remove(new HashMap.SimpleEntry<>(receiver, slot));
    }

    public void emit(Object sender, String signal, Object ...params) {
        HashSet<HashMap.SimpleEntry<Object, String>>
                receivers = connections.get(new HashMap.SimpleEntry<>(sender, signal));

        if (receivers == null)
            return;

        try {
            theSender = sender;
            for (HashMap.SimpleEntry<Object, String> snd: receivers)
                snd.getKey().getClass().getMethod(
                        snd.getValue()).invoke(snd.getKey(), params);
        }
        catch (NoSuchMethodException exc) {
            logger.registerLog(Logger.MsgType.INFO, "No such method! " +  exc.getMessage());
        }
        catch (InvocationTargetException exc) {
            logger.registerLog(Logger.MsgType.INFO, "No such receiver! " +  exc.getMessage());
        }
        catch (IllegalAccessException exc) {
            logger.registerLog(Logger.MsgType.INFO, "Do not try to be hacker: no private method watch! " +  exc.getMessage());
        }
    }

    public EventSystem getInstanceEventSystem() {
        return instanceEventSystem;
    }

    public Object sender() {
        return theSender;
    }

    private HashMap<
                HashMap.SimpleEntry<Object, String>,
            HashSet<HashMap.SimpleEntry<Object, String>>> connections;
    private Logger logger;

    private Object theSender;

    static private EventSystem instanceEventSystem;

}
