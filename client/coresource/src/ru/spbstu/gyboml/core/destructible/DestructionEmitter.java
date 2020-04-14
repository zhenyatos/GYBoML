package ru.spbstu.gyboml.core.destructible;

import java.util.ArrayList;
import java.util.List;

public class DestructionEmitter {
    private List<DestructionListener> listeners = new ArrayList<>();

    public void addListener(DestructionListener listener) {
        listeners.add(listener);
    }

    public void destruction(int newHP) {
        for (DestructionListener listener : listeners)
            listener.destructionOccured(newHP);
    }
}
