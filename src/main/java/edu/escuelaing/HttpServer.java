package edu.escuelaing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    public static Map<String, GetService> services = new HashMap<String, GetService>();
    private static Map<String, PostService> postRoutes = new HashMap<>();

    public static void addURIs(String uri, GetService method) {
        services.put(uri, method);
    }

    public static void addPost(String uri, PostService method) {
        postRoutes.put(uri, method);
    }

    public static void main(String[] args) {

        HttpServer.get("/hello", (request, response) -> {
            //
            return "Hello World";
        });
        HttpServer.get("/", (request, response) -> {
            //
            return getIndexResponse();
        });
        HttpServer.get("/imgg.png", (request, response) -> {
            //
            return "/imgg.png";
        });
        HttpServer.get("/img.jpg", (request, response) -> {
            //
            return "/img.jpg";
        });
        HttpServer.get("/imggg.jpg", (request, response) -> {
            //
            return "/imggg.jpg";
        });
        HttpServer.get("/index.html", (request, response) -> {
            //
            return "/index.html";
        });

        try {
            HttpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void start() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        Socket clientSocket = null;
        while (!serverSocket.isClosed()) {
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine = "";
            boolean firstline = true;
            String path = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if(firstline){
                    firstline = false;
                    path = inputLine.split(" ")[1];
                }
                if (!in.ready()) {
                    break;
                }
            }

            System.out.println("Path: " + path);

            String responseBody = "";

            for (String service : services.keySet()) {
                if (path.equals(service)) {
                    WebRequest request = new WebRequest();
                    WebResponse response = new WebResponse();
                    System.out.println(services.get(service).getMethod(request, response));
                    System.out.println(service);
                    if (services.get(service).getMethod(request, response).equals(service)) {
                        outputLine = searchFile(path, responseBody, outputLine, clientSocket);
                    } else {
                        services.get(service).getMethod(request, response);
                        responseBody = services.get(service).getMethod(request, response);
                        System.out.println("ResponseBody: " + responseBody);
                        outputLine = getLine(responseBody);
                    }
                }
            }

            out.println(outputLine);
            out.close();
            in.close();
        }
        clientSocket.close();
        serverSocket.close();
    }

    private static String searchFile(String path, String responseBody, String outputLine, Socket clientSocket) throws IOException {
        if (path != null && !getFile(path).equals("Not Found")) {
            responseBody = getFile(path);
            outputLine = getLine(responseBody);
        } else if (path != null && path.split("\\.")[1].equals("jpg") || path.split("\\.")[1].equals("png")) {
            OutputStream outputStream = clientSocket.getOutputStream();
            File file = new File("src/main/resources/img/" + path);
            BufferedImage bufferedImage = ImageIO.read(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            ImageIO.write(bufferedImage, path.split("\\.")[1], byteArrayOutputStream);
            outputLine = getImg("");
            dataOutputStream.writeBytes(outputLine);
            dataOutputStream.write(byteArrayOutputStream.toByteArray());
            System.out.println(outputLine);
        }
        return outputLine;
    }

    public static String getFile(String route) {
        Path file = FileSystems.getDefault().getPath("src/main/resources/img", route);
        Charset charset = Charset.forName("US-ASCII");
        String web = "";
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                web += line + "\n";
            }
        } catch (IOException x) {
            web = "Not Found";
        }
        return web;
    }

    public static String getIndexResponse() {
        return "HTTP/1.1 200 OK"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>MICROFRAMEWORKS WEB</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>MICROFRAMEWORKS WEB</h1>\n" +
                "    </body>\n" +
                "</html>";
    }

    public static String getLine(String responseBody) {
        return "HTTP/1.1 200 OK \r\n"
                + "Content-Type: text/html \r\n"
                + "\r\n"
                + "\n"
                + responseBody;
    }

    private static String getImg(String responseBody) {
        System.out.println("response Body" + responseBody);
        return "HTTP/1.1 200 OK \r\n"
                + "Content-Type: image/jpg \r\n"
                + "\r\n";
    }

    public static void get(String uri, GetService getService) {
        addURIs(uri, getService);
    }

    public static void post(String uri, PostService postService) {
        addPost(uri, postService);
    }
}
