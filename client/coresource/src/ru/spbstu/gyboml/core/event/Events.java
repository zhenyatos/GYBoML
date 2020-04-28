package ru.spbstu.gyboml.core.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;


public class Events {

    static {
        instance = new Events();
    }

    private Events() {
        connections = new HashMap<>();
    }

    public void connect(Object sender, Method signal, Object receiver, Method slot) {
        HashMap.SimpleEntry<Object, Method> key = new HashMap.SimpleEntry<>(sender, signal);

        HashSet<HashMap.SimpleEntry<Object, Method>> receivers = connections.get(
                new HashMap.SimpleEntry<>(sender, signal));

        if (receivers == null) {
            receivers = new HashSet<>();
            receivers.add(new HashMap.SimpleEntry<>(receiver, slot));
            connections.put(key, receivers);
        } else
            receivers.add(new HashMap.SimpleEntry<>(receiver, slot));
    }

    public void disconnect(Object sender, Method signal, Object receiver, Method slot) {
        HashSet<HashMap.SimpleEntry<Object, Method>> receivers = connections.get(
                new HashMap.SimpleEntry<>(sender, signal));

        if (receivers == null)
            return;

        receivers.remove(new HashMap.SimpleEntry<>(receiver, slot));
    }

    public void emit(Object sender, Method signal, Object... slotParams) {
        HashSet<HashMap.SimpleEntry<Object, Method>>
                receivers = connections.get(new HashMap.SimpleEntry<>(sender, signal));

        if (receivers == null)
            return;

        try {
            theSender = sender;
            for (HashMap.SimpleEntry<Object, Method> snd: receivers)
                    snd.getValue().invoke(snd.getKey(), slotParams);
        }
        catch (InvocationTargetException exc) {
            logger.registerLog(Logger.MsgType.INFO, "No such receiver! " +  exc.getMessage());
        }
        catch (IllegalAccessException exc) {
            logger.registerLog(Logger.MsgType.INFO, "Do not try to be hacker: no private method watch! " +  exc.getMessage());
        }
    }

    public static Events get() {
        return instance;
    }

    public Method find(Class cl, String name, Class ...args) {
        try {
            return cl.getMethod(name, args);
        } catch (NoSuchMethodException exc) {
            logger.registerLog(Logger.MsgType.INFO, "No such method! " +  exc.getMessage());
        }
        return null;
    }

    private HashMap<
                HashMap.SimpleEntry<Object, Method>,
            HashSet<HashMap.SimpleEntry<Object, Method>>> connections;
    private Logger logger;

    private Object theSender;

    static private Events instance;

}
