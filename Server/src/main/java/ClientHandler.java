import constants.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

public class ClientHandler {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private boolean authenticated;
    private String nickname;

    public String getLogin() {
        return login;
    }

    private String login;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            server.getExecutorService().execute(() -> {
                try {
                    socket.setSoTimeout(5000);
                    //цикл аутентификации
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals(Command.END)) {
                                sendMsg(Command.END);
                                break;
                            }

                            if (str.startsWith(Command.AUTH)) {
                                String[] token = str.split(" ", 3);
                                if (token.length < 3) {
                                    continue;
                                }
                                String newNick = server.getAuthService()
                                        .getNicknameByLoginAndPassword(token[1], token[2]);
                                login = token[1];
                                if (newNick != null) {
                                    if (!server.isLoginAuthenticated(login)){
                                        nickname = newNick;
                                        sendMsg(Command.AUTH_OK + " "+ nickname);
                                        authenticated = true;
                                        server.subscribe(this);
                                        logger.info("Client " + nickname+ " authenticated");
                                        break;
                                    }
                                    if (str.equals(Command.REG_OK)|| str.equals(Command.REG_NO)){

                                    }
                                    else{
                                        sendMsg("Уже произведен вход с данной учетной записью");
                                    }

                                } else {
                                    sendMsg("Логин / пароль не верны");
                                }
                            }
                            if (str.startsWith(Command.REG)) {
                                String[] token = str.split(" ");
                                if (token.length < 4) {
                                    continue;
                                }
                                if(server.getAuthService().registration(token[1],token[2],token[3])){
                                    sendMsg(Command.REG_OK);
                                }else{
                                    sendMsg(Command.REG_NO);
                                }
                            }
                        }
                    }
                    //цикл работы
                    while (authenticated) {
                        socket.setSoTimeout(0);
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals(Command.END)) {
                                sendMsg(Command.END);
                                break;
                            }
                            if (str.startsWith(Command.W)) {
                                String[] token = str.split(" ", 3);
                                if (token.length < 3) {
                                    continue;
                                }
                                server.privateMsg(this,token[1],token[2]);
                            }

                            if (str.startsWith(Command.CHANGENICK)){
                                String[] token = str.split("\\s+",2);
                                if (token.length<2){
                                    continue;
                                }
                                if (token[1].contains(" ")){
                                    sendMsg("nick doesn't contain space simbols");
                                    continue;
                                }
                                if (server.getAuthService().changeNickname(this.nickname,token[1])){
                                    sendMsg("/yournickis " + token[1]);
                                    sendMsg("your nick updated to " + token[1]);
                                    this.nickname = token[1];
                                    server.broadcastClientList();
                                }else{
                                    sendMsg("can't update nick. This nick is busy");
                                }
                            }

                        }else {
                            server.broadcastMsg(this, str);
                        }



                    }
                }catch (SocketTimeoutException e){
                    sendMsg(Command.END);
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    System.out.println("Client disconnected");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }
}
