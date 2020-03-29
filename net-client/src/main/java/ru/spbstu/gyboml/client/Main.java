package ru.spbstu.gyboml.client;

import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static void main( String[] args ) throws SocketException, UnknownHostException {
        Controller mainController = new Controller("localhost", 4445);

        mainController.run();
    }
}
