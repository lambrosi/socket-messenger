package com.ulbra.app.server;

import com.ulbra.app.io.CronSaveHistory;
import com.ulbra.app.io.MessageHistory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class MessengerServer {

    private static final Integer SERVER_PORT = 8976;
    private static final String MESSAGE_COMMAND = "MSG";
    private static Integer COUNT = 0;
    private static Map<String, PrintWriter> usersOutputs;
    private static List<String> messageHistory;

    public MessengerServer() {
        usersOutputs = new HashMap<>();
        messageHistory = MessageHistory.getHistory();
    }

    public void start(){
        try (ServerSocket server = new ServerSocket(SERVER_PORT)){
            new CronSaveHistory(messageHistory).start();
            for(;;){
                new UserClient(server.accept()).start();
                COUNT++;
                printAmountActiveClients();

            }
        } catch (IOException ex) {
            System.out.println("Erro na conexao: " + ex.getMessage());
        }
    }

    private void printAmountActiveClients(){
        System.out.println("Active clients: " + COUNT);
    }


    private class UserClient extends Thread{

        private Socket socket;
        private String nickname;

        private UserClient(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Scanner input = new Scanner(new InputStreamReader(socket.getInputStream()));
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true)){

                String line;
                while ((line = input.nextLine()) != null) {
                    if (line.length() == 0) break;

                    String option = line.toUpperCase();

                    if(option.startsWith("ENTRAR")){
                        nickname = extractNickname(line);
                        if(isNicknameAvailable(nickname)){
                            loginChat(nickname, output);
                            notifyAllClients(nickname, true);
                            loadLastTwentyMessages(output);
                        } else {
                            output.println("ERRO Nickname em uso.");
                        }

                    } else if(option.startsWith("MSG")){
                        sendMessageToAllClients(nickname, line);

                    } else if(option.startsWith("SAIR")){
                        logoutChat(nickname);
                        notifyAllClients(nickname, false);
                        break;

                    } else {
                        output.println("Comando invalido.");
                    }
                }
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }

        private synchronized void loginChat(String nickname, PrintWriter output) {
            usersOutputs.put(nickname, output);
        }

        private synchronized void logoutChat(String nickname) {
            usersOutputs.remove(nickname);
            COUNT--;
            printAmountActiveClients();
            Thread.currentThread().interrupt();
        }

        private void sendMessageToAllClients(String nickname, String line) {
            String messageContent = line.substring(line.indexOf(" ") + 1);

            String outputMessage = String.format("%s %s %s\n", MESSAGE_COMMAND, nickname, messageContent);
            messageHistory.add(outputMessage);

            for (PrintWriter output : usersOutputs.values()){
                output.printf(outputMessage);
            }
        }

        private synchronized void loadLastTwentyMessages(PrintWriter output) {
            if(messageHistory.isEmpty()){
                output.println("Nenhuma mensagem antiga.");
            }else if(messageHistory.size() < 20){
                for(String message : messageHistory){
                    output.printf("%s", message);
                }
            } else {
                int count = 0;
                for(int i = messageHistory.size() - 20; count < 20; i++, count++){
                    output.println(messageHistory.get(i));
                }
            }
        }

        private void notifyAllClients(String nickname, boolean login) {
            String status = login ? " entrou" : " saiu";

            for(PrintWriter output : usersOutputs.values()){
                output.printf("%s %s %s no grupo.\n",
                        MESSAGE_COMMAND, nickname, status);
            }
        }
        private String extractNickname(String linha){
            return linha.substring(linha.indexOf(" ") + 1);
        }

        private synchronized boolean isNicknameAvailable(String nickname) {
            return !usersOutputs.containsKey(nickname);
        }
    }

}
