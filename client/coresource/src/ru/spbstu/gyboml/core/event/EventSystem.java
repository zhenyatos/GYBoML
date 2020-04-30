package ru.spbstu.gyboml.core.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;


public class EventSystem {

    static {
        instanceEventSystem = new EventSystem();
    }

    private EventSystem() {
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
                                return false;
                            }
                            for (int i = 0; i < signalParams.length; i++)
                                if (!signalParams[i].getCanonicalName().
                                        equals(slotParams[i].getCanonicalName())) {
                                    return false;
                                }
                            methodReceiver = mReceiver;
                            break;
                        }

                    methodSender = mSender;
                    break;
                }

            HashMap.SimpleEntry<Object, String>
                    key = new HashMap.SimpleEntry<>(sender, signal);

            HashSet<HashMap.SimpleEntry<Object, String>> receivers = new HashSet<>();

            if (connections.containsKey(key))
                receivers = connections.get(key);

            if (receivers != null)
                receivers.add(new HashMap.SimpleEntry<>(receiver, slot));
            else {
                return false;
            }

            connections.putIfAbsent(key, receivers);
            methods.put(new HashMap.SimpleEntry<>(receiver, slot), methodReceiver);
        }
        catch (NullPointerException exc) {
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
                if (m != null)
                    m.invoke(rec.getKey(), params);
            }
        }
        catch (NullPointerException | IllegalArgumentException exc) {
        }
        catch (InvocationTargetException exc) {
        }
        catch (IllegalAccessException exc) {
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

    private Object theSender;

    static private EventSystem instanceEventSystem;
}
