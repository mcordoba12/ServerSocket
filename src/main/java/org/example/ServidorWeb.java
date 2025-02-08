package org.example;

import java.io.*;
import java.net.*;

public class ServidorWeb {
    private ServerSocket servidor;

    public ServidorWeb(int puerto) throws IOException {
        servidor = new ServerSocket(puerto);
        System.out.println("Servidor conectado en el puerto " + puerto);
    }

    public void iniciar() throws IOException {
        while (true) {
            Socket socketCliente = servidor.accept();
            Thread hilo = new Thread(new SolicitudHttp(socketCliente));
            hilo.start();
        }
    }

}
