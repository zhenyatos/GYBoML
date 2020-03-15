package ru.spbstu.gyboml.server;

import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.packing.PacketType;
import ru.spbstu.gyboml.server.handling.Handler;
import ru.spbstu.gyboml.server.handling.HandlerManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

/**
 * Class controls all server-side computations.
 * It can be interpreted as single game session.
 */
public class Controller implements Runnable {

    // server port (TODO: move this in Godfather)
    public static final int port = 4445;

    // players objects
    Player firstPlayer;
    Player secondPlayer;

    InetAddress firstAddress = null;
    InetAddress secondAddress = null;

    // offices
    OfficeInput officeInput;
    OfficeOutput officeOutput;

    // handler
    HandlerManager handlerManager;

    // main server socket
    DatagramSocket socket;

    // packet queues
    Queue<DatagramPacket> inputQueue;
    Queue<DatagramPacket> outputQueue;

    // mapping from packet type to its handler (parser)
    static final Map<PacketType, Handler> handlerMap;

    static {
        handlerMap = new HashMap<>();
    }

    // controller status
    enum Status {
        OK,
        FAILED;
    }
    Status status = Status.OK;

    /**
     * Class constructor.
     *
     * Creates datagram socket and initializes offices.
     * */
    Controller() {
        try {
            inputQueue = new ArrayDeque<>();
            outputQueue = new ArrayDeque<>();

            socket = new DatagramSocket(port);
            handlerManager = new HandlerManager(this);
            officeInput = new OfficeInput(socket, handlerManager, inputQueue);
            officeOutput = new OfficeOutput(socket, outputQueue);
        } catch (SocketException error) {
            status = Status.FAILED;
            System.out.println("Could not create socket");
        }
    }

    /**
     * Start controller.
     *
     */
    @Override
    public void run() {

        //start threads
        officeInput.start();
        officeOutput.start();
        handlerManager.start();

        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        while (running) {
            // TODO: move CLI controls to Godfather
            System.out.print("control> ");
            String input = scanner.nextLine();

            // exit condition
            if (input.equals("q")) {
                running = false;
                officeInput.interrupt();
                officeOutput.interrupt();
                handlerManager.interrupt();
                socket.close();
            }
        }
    }

    public Queue<DatagramPacket> getInputQueue() {
        return this.inputQueue;
    }

    public Queue<DatagramPacket> getOutputQueue() {
        return this.outputQueue;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public void createFirstPlayer( InetAddress address ) {
        if (firstPlayer != null) {
            return;
        }

        firstPlayer = new Player(0, true);
        firstAddress = address;
    }

    public void createSecondPlayer( InetAddress address ) {
        if (secondPlayer != null) {
            return;
        }

        secondPlayer = new Player(0, false);
        secondAddress = address;
    }

    public OfficeOutput getOfficeOutput() {
        return this.officeOutput;
    }
}
