package org.example;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader userInput;
    private String clientName;

    public ChatClient(String serverAddress, int port) {
        try {
            // Se connecter au serveur
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userInput = new BufferedReader(new InputStreamReader(System.in));

            // Lire le message initial du serveur (demande du nom)
            System.out.println(in.readLine());
            clientName = userInput.readLine();
            out.println(clientName); // Envoyer le nom au serveur

            // Démarrer un thread pour écouter les messages du serveur, y compris l'historique
            new Thread(new ServerListener(in, clientName)).start();

            // Envoyer les messages du client au serveur
            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message); // Envoyer le message au serveur
                if ("bye".equalsIgnoreCase(message)) {
                    break;
                }
            }
            stop(); // Fermer la connexion après "bye"
        } catch (IOException e) {
            System.out.println("Erreur de connexion au serveur : " + e.getMessage());
        }
    }

    // Fermer toutes les ressources
    public void stop() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Déconnecté du serveur.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la fermeture du client : " + e.getMessage());
        }
    }

    // Classe interne pour écouter les messages du serveur
    private static class ServerListener implements Runnable {
        private BufferedReader in;
        private String clientName;

        public ServerListener(BufferedReader in, String clientName) {
            this.in = in;
            this.clientName = clientName;
        }

        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    // Afficher tous les messages reçus du serveur
                    // La vérification ici est modifiée pour gérer correctement l'historique
                    if (!serverMessage.startsWith(clientName + ":")) {
                        System.out.println(serverMessage); // Afficher le message s'il ne vient pas du client lui-même
                    }
                }
            } catch (IOException e) {
                System.out.println("Connexion au serveur perdue : " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // Adresse et port du serveur (adapter si nécessaire)
        String serverAddress = "localhost";
        int port = 6667;
        new ChatClient(serverAddress, port); // Démarrer le client
    }
}
