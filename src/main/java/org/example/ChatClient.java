package org.example;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader userInput;

    public ChatClient(String serverAddress, int port) {
        try {
            // Se connecter au serveur
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userInput = new BufferedReader(new InputStreamReader(System.in));

            // Lire le message initial du serveur (demande du nom)
            System.out.println(in.readLine());
            String name = userInput.readLine();
            out.println(name); // Envoyer le nom au serveur

            // Démarrer un thread pour écouter les messages du serveur
            new Thread(new ServerListener(in)).start();

            // Envoyer les messages du client au serveur
            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);
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

        public ServerListener(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage); // Afficher le message du serveur
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
