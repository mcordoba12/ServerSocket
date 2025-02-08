package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            ServidorWeb servidor = new ServidorWeb(8080);
            servidor.iniciar();
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e);
        }
    }
}
