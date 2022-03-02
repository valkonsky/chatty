import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) {
        Socket socket = null;
        Scanner scanner = new Scanner(System.in);
        try(ServerSocket serverSocket = new ServerSocket(8189)){
            System.out.println("server running...");
            socket = serverSocket.accept();
            System.out.println("Client connected");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            Thread thread = new Thread(()->{
                while (true){
                    try {
                        out.writeUTF(scanner.nextLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

            thread.setDaemon(true);
            thread.start();

            while(true){
                String str = in.readUTF();
                if (str.equals("/end")){
                    System.out.println("client disconnected");
                    out.writeUTF("/end");
                    break;
                }else{
                    System.out.println("Client: " +str);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }

    }
}
