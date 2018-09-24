package com.ulbra.app;

import com.ulbra.app.server.MessengerServer;

public class Main {

    public static void main(String[] args) {
        MessengerServer server = new MessengerServer();
        server.start();
    }
}
