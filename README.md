Este programa simula un servidor HTTP que maneja solicitudes `GET`. Cuando un cliente se conecta, el servidor procesa la solicitud y busca el archivo solicitado. Si el archivo existe, lo devuelve con un estado HTTP `200 OK`, su tipo MIME y tamaño. Si el archivo no existe, responde con un error `404 Not Found` y un archivo de error HTML.

### Funciones principales:
- **`run()`**: Inicia el procesamiento de la solicitud HTTP en un hilo.
- **`procesarSolicitud()`**: Lee la solicitud, determina el archivo solicitado y devuelve la respuesta adecuada.
- **`enviarString()` y `enviarBytes()`**: Envían la respuesta (encabezados y contenido) al cliente.
- **`contentType()`**: Determina el tipo MIME basado en la extensión del archivo.

Si un archivo no se encuentra, el servidor devuelve un mensaje de error 404. Este servidor es útil para aprender sobre la implementación de un servidor HTTP básico.
