package org.example;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private ServerSocket serverSocket;
    private ExecutorService pool;
    // Liste des flux de sortie de tous les clients connectés
    private List<PrintWriter> clientOutputs = Collections.synchronizedList(new ArrayList<>());
    // Liste pour stocker l'historique des messages
    private List<String> messageHistory = Collections.synchronizedList(new ArrayList<>());

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newCachedThreadPool();
        System.out.println("Serveur démarré et écoute sur le port " + port);

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté depuis " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                System.out.println("Erreur lors de l'acceptation d'une connexion client : " + e.getMessage());
            }
        }
    }

    public void stop() throws IOException {
        pool.shutdown();
        serverSocket.close();
        System.out.println("Serveur arrêté.");
    }

    // Classe interne pour gérer la communication avec chaque client
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Ajouter le flux de sortie à la liste des clients connectés
                synchronized (clientOutputs) {
                    clientOutputs.add(out);
                }

                // Envoyer l'historique des messages au nouveau client
                sendHistoryToClient();

                out.println("Entrez votre nom :");
                clientName = in.readLine();
                System.out.println(clientName + " s'est connecté.");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("bye")) {
                        out.println("Déconnexion du serveur...");
                        break;
                    }
                    String formattedMessage = clientName + ": " + message;
                    // Ajouter le message à l'historique
                    addMessageToHistory(formattedMessage);

                    // Afficher le message du client sur le serveur
                    System.out.println(formattedMessage);

                    // Diffuser le message à tous les autres clients
                    broadcastMessage(formattedMessage);
                }
            } catch (IOException e) {
                System.out.println("Erreur de communication avec le client " + (clientName != null ? clientName : "") + ": " + e.getMessage());
            } finally {
                try {
                    // Retirer le flux de sortie de la liste des clients connectés
                    synchronized (clientOutputs) {
                        clientOutputs.remove(out);
                    }
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (clientSocket != null) clientSocket.close();
                    System.out.println((clientName != null ? clientName : "Client") + " s'est déconnecté.");
                } catch (IOException e) {
                    System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
                }
            }
        }

        // Méthode pour envoyer l'historique des messages au client
        private void sendHistoryToClient() {
            synchronized (messageHistory) {
                for (String pastMessage : messageHistory) {
                    out.println(pastMessage);
                }
            }
        }

        // Méthode pour ajouter un message à l'historique
        private void addMessageToHistory(String message) {
            synchronized (messageHistory) {
                messageHistory.add(message);
            }
        }

        // Méthode pour diffuser un message à tous les clients
        private void broadcastMessage(String message) {
            synchronized (clientOutputs) {
                for (PrintWriter writer : clientOutputs) {
                    writer.println(message);
                }
            }
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        try {
            server.start(6667);
        } catch (IOException e) {
            System.out.println("Erreur lors du démarrage du serveur : " + e.getMessage());
        }
    }
}
