package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServerGPTVersion {
    private static final int PORT = 12345; // Le port sur lequel le serveur écoute
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur de chat démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté");

                // Créer un gestionnaire de client dans un thread séparé
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour diffuser un message à tous les clients
    public static void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    // Classe interne pour gérer les clients
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Initialiser les flux de communication
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Demander le nom du client
                out.println("Entrez votre nom :");
                clientName = in.readLine();
                System.out.println(clientName + " a rejoint le chat.");

                // Lire et afficher les messages du client
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(clientName + ": " + message);
                    // Diffuser le message à tous les clients
                    broadcastMessage(clientName + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Envoyer un message au client
        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
