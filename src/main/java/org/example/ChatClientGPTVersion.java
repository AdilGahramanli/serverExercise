package org.example;

import java.io.*;
import java.net.*;

public class ChatClientGPTVersion {
    private static final String SERVER_ADDRESS = "localhost"; // Adresse du serveur
    private static final int SERVER_PORT = 12345; // Port du serveur

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Lire le message initial du serveur (demande du nom)
            System.out.println(in.readLine());
            String name = consoleInput.readLine();
            out.println(name); // Envoyer le nom au serveur

            // Démarrer un thread pour écouter les messages du serveur
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Lire les messages de l'utilisateur et les envoyer au serveur
            String userMessage;
            while ((userMessage = consoleInput.readLine()) != null) {
                out.println(userMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
