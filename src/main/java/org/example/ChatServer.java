package org.example;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private ServerSocket serverSocket;
    private ExecutorService pool;

    public void start(int port) throws IOException {
        // Démarre le serveur sur le port spécifié et initialise le pool de threads
        serverSocket = new ServerSocket(port);
        pool = Executors.newCachedThreadPool(); // Utilise un pool de threads pour gérer plusieurs clients

        // Afficher un message indiquant que le serveur tourne et le port utilisé
        System.out.println("Serveur démarré et écoute sur le port " + port);

        // Boucle continue pour accepter les connexions clients
        while (true) {
            try {
                // Accepter une nouvelle connexion client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté depuis " + clientSocket.getInetAddress());

                // Lancer un nouveau thread pour gérer la communication avec ce client
//                pool.execute(new ClientHandler(clientSocket));
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                System.out.println("Erreur lors de l'acceptation d'une connexion client : " + e.getMessage());
            }
        }
    }

    public void stop() throws IOException {
        // Arrête le pool de threads et ferme le socket serveur
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
                // Initialiser les flux de communication avec le client
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Demander le nom du client et l'afficher sur le serveur
                out.println("Entrez votre nom :");
                clientName = in.readLine();
                System.out.println(clientName + " s'est connecté.");

                // Écouter les messages du client de manière continue
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("bye")) {
                        out.println("Déconnexion du serveur...");
                        break;
                    }
                    // Afficher le message du client sur le serveur et répondre
                    System.out.println(clientName + ": " + message);
                    out.println("Message reçu : " + message);
                }
            } catch (IOException e) {
                System.out.println("Erreur de communication avec le client : " + e.getMessage());
            } finally {
                try {
                    // Fermer les ressources de communication et informer que le client s'est déconnecté
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (clientSocket != null) clientSocket.close();
                    System.out.println(clientName + " s'est déconnecté.");
                } catch (IOException e) {
                    System.out.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        try {
            // Démarrer le serveur sur le port 6666
            server.start(6667);
        } catch (IOException e) {
            System.out.println("Erreur lors du démarrage du serveur : " + e.getMessage());
        }
    }
}
