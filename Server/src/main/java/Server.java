import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static ServerSocket server;
    private static Socket socket;
    private final int PORT= 8189;

    private List<ClientHandler> clients;
    private AuthService authService;

    public ExecutorService getExecutorService() {
        return executorService;
    }

    private ExecutorService executorService;

    public Server() {
        executorService = Executors.newCachedThreadPool();
        clients = new CopyOnWriteArrayList<>();

        if (!SQLHandler.connect()){
            throw new RuntimeException("access to database aborted");
        }
        authService = new DBAuthService();
        try {
            server = new ServerSocket(PORT);
            logger.info("server started");

            while(true){
                socket = server.accept();
                logger.info("Client connected");
                new ClientHandler(this,socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE,e.getMessage(),e);
        }finally {
            executorService.shutdown();
            SQLHandler.disconnect();
            try {
                socket.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
            }
            try {
                server.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
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
