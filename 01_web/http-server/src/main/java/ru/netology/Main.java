package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png",
                "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html",
                "/classic.html", "/events.html", "/events.js");

        final var port = 9999;
        Server server = new Server(port, validPaths);

        server.addHandler("GET", "/messages", (request,
                                               responseStream) -> {
            String lastParam = request.getQueryParam("last");
            int last = 10;

            if (lastParam != null) {
                try {
                    last = Integer.parseInt(lastParam);
                } catch (NumberFormatException e) {

                }
            }

            String jsonResponse = "{\"messages\": [\"msg1\", \"msg2\"], \"last\": " + last + "}";

            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + jsonResponse.getBytes().length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    jsonResponse;

            responseStream.write(response.getBytes());
            responseStream.flush();
        });
        server.start();
    }
}