package ru.spbstu.gyboml.core.net;

import ru.spbstu.gyboml.core.net.office.OfficeInput;
import ru.spbstu.gyboml.core.net.office.OfficeOutput;

import java.net.DatagramPacket;
import java.util.Queue;

/**
 * Controller interface, assuming it have two specific queues,
 * input packet queue and output packet queue
 */
public interface ControllerInterface {
    Queue<DatagramPacket> getInputQueue();
    Queue<DatagramPacket> getOutputQueue();
    OfficeOutput getOfficeOutput();
    OfficeInput getOfficeInput();
}
