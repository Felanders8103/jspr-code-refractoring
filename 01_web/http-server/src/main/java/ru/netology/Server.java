package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final ExecutorService threadPool;
    private final List<String> validPaths;
    private final Map<String, Handler> handlers = new HashMap<>();

    public Server(int port, List<String> validPaths) {
        this.port = port;
        this.validPaths = validPaths;
        this.threadPool = Executors.newFixedThreadPool(64);
    }

    public void addHandler(String method, String path, Handler handler) {
        String key = method + "_" + path;
        handlers.put(key, handler);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> {
                    try {
                        handleConnection(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleConnection(Socket clientSocket) throws IOException {
        try (
                var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                var out = new BufferedOutputStream(clientSocket.getOutputStream());
        ) {
            final var requestLine = in.readLine();

            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }

            System.out.println("requestLine = " + requestLine);

            Request request;
            try {
                request = new Request(requestLine);
                System.out.println("Парсинг запроса: " + request);
            } catch (URISyntaxException e) {
                out.write(("HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n").getBytes());
                out.flush();
                return;
            }

            String handlerKey = request.getMethod() + "_" + request.getPath();
            Handler handler = handlers.get(handlerKey);

            if (handler != null) {
                handler.handle(request, out);
                return;
            }

            handleStaticFile(request, out);

        }
    }

    private void handleStaticFile(Request request, BufferedOutputStream out) throws IOException {
        String path = request.getPath();

        if (!validPaths.contains(path)) {
            out.write(("HTTP/1.1 404 Not Found\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection: close\r\n" +
                    "\r\n").getBytes());
            out.flush();
            return;
        }

        final var filePath = Path.of(".", "public", path);
        final var mimeType = Files.probeContentType(filePath);
        System.out.println("mimeType = " + mimeType);

        if (path.equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();

            out.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + mimeType + "\r\n" +
                    "Content-Length: " + content.length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n").getBytes());
            out.write(content);
            out.flush();
            return;
        }
        
        final var length = Files.size(filePath);
        System.out.println("Пишу заголовки");
        out.write(("HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n").getBytes());
        Files.copy(filePath, out);
        out.flush();
    }
}