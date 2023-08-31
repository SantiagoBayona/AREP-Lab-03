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

public class HttpServer {

    public static void main(String[] args) throws IOException {
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
            String inputLine, outputLine;
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

            if (path != null && path.equals("/")) {
                responseBody = "DISEÑO Y ESTRUCTURACIÓN DE APLICACIONES DISTRIBUIDAS EN INTERNET";
                outputLine = getLine(responseBody);
            } else if (path != null && !getFile(path).equals("Not Found")) {
                responseBody = getFile(path);
                outputLine = getLine(responseBody);
            } else if (path != null && path.split("\\.")[1].equals("jpg") ||
                    path.split("\\.")[1].equals("png")) {
                OutputStream outputStream = clientSocket.getOutputStream();
                File file = new File("src/main/resources/img/" + path);
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                    ImageIO.write(bufferedImage, path.split("\\.")[1], byteArrayOutputStream);
                    outputLine = getImg("");
                    dataOutputStream.writeBytes(outputLine);
                    dataOutputStream.write(byteArrayOutputStream.toByteArray());
                    System.out.println(outputLine);
                } catch (IOException e) {
                    e.printStackTrace();
                    responseBody = getFile(path);
                    outputLine = getLine(responseBody);
                }
            } else {
                outputLine = getIndexResponse();
            }
            out.println(outputLine);
            out.close();
            in.close();
        }
        clientSocket.close();
        serverSocket.close();
    }

    public static String getFile(String route) {
        Path file = FileSystems.getDefault().getPath("src/main/resources/img", route);
        Charset charset = Charset.forName("US-ASCII");
        String web = new String();
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
        String response = "HTTP/1.1 200 OK"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>DISEÑO Y ESTRUCTURACIÓN DE APLICACIONES DISTRIBUIDAS EN INTERNET</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>DISEÑO Y ESTRUCTURACIÓN DE APLICACIONES DISTRIBUIDAS EN INTERNET</h1>\n" +
                "    </body>\n" +
                "</html>";
        return response;
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
}
