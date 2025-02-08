package org.example;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class SolicitudHttp implements Runnable {
    final static String CRLF = "\r\n";

    //para leer y escribir datos en la comunicación con el cliente.
    private Socket socket;
    public SolicitudHttp(Socket socket) {
        this.socket = socket;
    }


    //para manejar la solicitud HTTP del cliente
    @Override
    public void run() {
        try {
            procesarSolicitud();
        } catch (Exception e) {
            System.out.println("Error al procesar la solicitud: " + e);
        }
    }


    //para procesar la solicitud HTTP del cliente
    private void procesarSolicitud() throws Exception {
        try (
                //para leer y escribir datos en la comunicación con el cliente.
                BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String lineaDeSolicitud;

            // Procesar la solicitud HTTP
            while ((lineaDeSolicitud = in.readLine()) != null) {
                System.out.println("Recibiendo solicitud: " + lineaDeSolicitud);

                // para dividir la solicitud en partes.
                StringTokenizer partesLinea = new StringTokenizer(lineaDeSolicitud);
                String method = partesLinea.nextToken(); // Método (GET)

                // Verificar que el método sea "GET"
                if (method.equals("GET")) {
                    // Extraer el recurso solicitado
                    String nombreArchivo = partesLinea.nextToken();
                    System.out.println("Recurso solicitado: " + nombreArchivo);

                    // Si el recurso solicitado es "/", se busca index.html
                    if (nombreArchivo.equals("/")) {
                        nombreArchivo = "/index.html";
                    }

                    // Buscar el archivo solicitado desde resources
                    InputStream inputStream = getClass().getResourceAsStream(nombreArchivo);
                    long filesize = 0;
                    String lineaDeEstado;
                    String lineaDeTipoContenido;

                    if (inputStream != null) {
                        // Si el archivo existe, obtenemos el tamaño y el tipo MIME
                        filesize = inputStream.available();
                        lineaDeEstado = "HTTP/1.0 200 OK\r\n";
                        lineaDeTipoContenido = "Content-type: " + contentType(nombreArchivo) + CRLF;
                        System.out.println("Archivo encontrado: " + nombreArchivo);
                    } else {
                        // Si el archivo no se encuentra, responder con error 404
                        lineaDeEstado = "HTTP/1.0 404 Not Found\r\n";
                        lineaDeTipoContenido = "Content-type: text/html" + CRLF;
                        System.out.println("Archivo no encontrado: " + nombreArchivo);

                        inputStream = getClass().getResourceAsStream("/404.html");
                        if (inputStream == null) {
                            // Si el archivo 404.html no se encuentra, usamos un mensaje simple
                            String errorMessage = "<html><body><h1>404 Not Found</h1></body></html>";
                            inputStream = new ByteArrayInputStream(errorMessage.getBytes(StandardCharsets.UTF_8));
                            filesize = errorMessage.length();
                        } else {
                            filesize = inputStream.available();
                        }
                    }

                    // Enviar la línea de estado
                    enviarString(lineaDeEstado, out);

                    // Enviar el header, incluyendo el Content-Length
                    enviarString("Content-Length: " + filesize + CRLF, out);
                    enviarString(lineaDeTipoContenido, out);
                    enviarString(CRLF, out);
                    enviarBytes(inputStream, out);

                    out.flush();
                } else {
                    // Si el método no es GET, simplemente cerramos la conexión sin hacer nada
                    break;
                }

                // Leer los encabezados HTTP
                while ((lineaDeSolicitud = in.readLine()) != null && !lineaDeSolicitud.isEmpty()) {
                    System.out.println("Encabezado recibido: " + lineaDeSolicitud);
                }
            }
        } finally {
            // Cerrar la conexión después de procesar todas las solicitudes
            socket.close();
        }
    }

    // Método para enviar un String al cliente
    private static void enviarString(String line, OutputStream os) throws Exception {
        os.write(line.getBytes(StandardCharsets.UTF_8));
    }

    // Método para enviar bytes (contenido de un archivo)
    private static void enviarBytes(InputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    // Método para determinar el tipo de contenido basado en la extensión del archivo
    private static String contentType(String nombreArchivo) {
        if (nombreArchivo.endsWith(".html") || nombreArchivo.endsWith(".htm")) {
            return "text/html";
        }
        if (nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (nombreArchivo.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream"; // Para cualquier otro tipo de archivo desconocido
    }
}
