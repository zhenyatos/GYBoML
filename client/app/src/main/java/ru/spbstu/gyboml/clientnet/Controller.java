package main.java.ru.spbstu.gyboml.clientnet;

import main.java.ru.spbstu.gyboml.clientnet.generating.ConnectionGenerator;
import main.java.ru.spbstu.gyboml.clientnet.generating.PassTurnGenerator;
import main.java.ru.spbstu.gyboml.clientnet.handling.PlayerUpdateHandler;
import ru.spbstu.gyboml.core.Player;
import ru.spbstu.gyboml.core.net.ControllerInterface;
import ru.spbstu.gyboml.core.net.handling.HandlerManager;
import ru.spbstu.gyboml.core.net.office.OfficeInput;
import ru.spbstu.gyboml.core.net.office.OfficeOutput;
import ru.spbstu.gyboml.core.net.packing.PacketType;

import java.net.*;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

/**
 * Client controller class.
 */
public class Controller extends Thread implements ControllerInterface {

    // address of remote server
    private final InetAddress serverAddress;
    private final int port;

    // datagram socket
    private DatagramSocket socket;

    // my player object
    private Player myPlayer;

    // packet queues
    private Queue<DatagramPacket> inputQueue;
    private Queue<DatagramPacket> outputQueue;

    private HandlerManager handlerManager;

    private OfficeInput officeInput;
    private OfficeOutput officeOutput;

    public Controller( String serverName, int port ) throws UnknownHostException, SocketException {
        this.serverAddress = InetAddress.getByName(serverName);
        this.inputQueue = new ArrayDeque<>();
        this.outputQueue = new ArrayDeque<>();
        this.port = port;

        this.socket = new DatagramSocket();
        this.handlerManager = new HandlerManager(this);

        // fill in handler map
        this.handlerManager.putHandler(PacketType.PLAYER_UPDATE, new PlayerUpdateHandler());

        this.officeInput = new OfficeInput(socket, handlerManager, inputQueue);
        this.officeOutput = new OfficeOutput(socket, outputQueue);
    }

    @Override
    public Queue<DatagramPacket> getInputQueue() {
        return inputQueue;
    }

    @Override
    public Queue<DatagramPacket> getOutputQueue() {
        return outputQueue;
    }

    @Override
    public OfficeOutput getOfficeOutput() {
        return officeOutput;
    }

    @Override
    public OfficeInput getOfficeInput() {
        return officeInput;
    }

    @Override
    public void run() {
        officeInput.start();
        officeOutput.start();
        handlerManager.start();

        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        while (running && !isInterrupted()) {
            System.out.print("control> ");
            String input = scanner.nextLine();

            if (input.equals("q")) {
                running = false;
                officeInput.interrupt();
                officeOutput.interrupt();
                handlerManager.interrupt();
                socket.close();
            } else if (input.equals("req")) {
                ConnectionGenerator generator = new ConnectionGenerator();
                generator.generate(null, serverAddress, port, this);
            } else if (input.equals("pass")) {
                PassTurnGenerator generator = new PassTurnGenerator();
                generator.generate(null, serverAddress, port, this);
            }
        }
    }

    public void setPlayer( Player player ) {
        myPlayer = player;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return port;
    }

}
