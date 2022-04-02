import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static ServerSocket server;
    private static Socket socket;
    private final int PORT= 8189;

    private List<ClientHandler> clients;
    private AuthService authService;

    public Server() {
        clients = new CopyOnWriteArrayList<>();

        if (!SQLHandler.connect()){
            throw new RuntimeException("access to database aborted");
        }
        authService = new DBAuthService();
        try {
            server = new ServerSocket(PORT);
            System.out.println("server started");

            while(true){
                socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(this,socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            SQLHandler.disconnect();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(ClientHandler sender, String msg){
       String message = String.format("[ %s ]: %s", sender.getNickname(), msg);

        for (ClientHandler client : clients) {
            client.sendMsg(message);
        }
    }

    public void privateMsg(ClientHandler sender,String recipient, String msg){
        String message = String.format("from [ %s ] to [ %s ]: %s", sender.getNickname(),recipient, msg);

        for (ClientHandler client : clients) {
            if (client.getNickname().equals(recipient)){
                client.sendMsg(message);
                if (!sender.getNickname().equals(recipient)) {
                    sender.sendMsg(message);
                }
                return;
            }

        }

        sender.sendMsg("user not found:" + recipient);
    }

    public boolean isLoginAuthenticated(String login){
        for (ClientHandler client : clients) {
            if (client.getLogin().equals(login)){
                return true;
            }
        }return false;
    }

    public void broadcastClientList(){
        StringBuilder sb = new StringBuilder("/clientlist");

        for (ClientHandler c : clients) {
            sb.append(" ").append(c.getNickname());
        }

        String msg = sb.toString();

        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }

    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcastClientList();
    }
    public AuthService getAuthService() {
        return authService;

    }
}
