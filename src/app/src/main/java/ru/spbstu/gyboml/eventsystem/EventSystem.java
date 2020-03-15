package ru.spbstu.gyboml.eventsystem;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventSystem {

    public boolean connect(Object sender, String signal, Object receiver, String Slot) {

    }

    public void disconnect(Object sender, String signal, Object receiver, String Slot) {

    }

    public void emit(Object sender, String signal, Object ...params) {
        ArrayList<HashMap.SimpleEntry<Object, String>> receivers = connections.get(
                new HashMap.SimpleEntry<>(sender, signal));

        if (receivers == null)
            return;

        try {
            int sz = receivers.size();
            for (int i = 0; i < sz; i++) {
                HashMap.SimpleEntry<Object, String>
                        snd = receivers.get(i);
                snd.getKey().getClass().getMethod(
                        snd.getValue()).invoke(snd.getKey(), params);
            }
        }
        catch (NoSuchMethodException exc) {
            return;
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
            ArrayList<HashMap.SimpleEntry<Object, String>>> connections;

    private Object theSender;

    static private EventSystem instanceEventSystem;

}
