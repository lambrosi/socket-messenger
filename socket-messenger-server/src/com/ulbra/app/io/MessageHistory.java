package com.ulbra.app.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MessageHistory {

    private static final String FILE_NAME = "messages.txt";
    private static final String FOLDER_NAME = "chat-history";
    private static String FULL_PATH;

    static {
        FULL_PATH = getHomePath() + File.separator + FOLDER_NAME + File.separator + FILE_NAME;
    }

    public static List<String> getHistory(){
        File file = new File(FULL_PATH);
        if(file.exists()){
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                return (List<String>) inputStream.readObject();
            } catch (FileNotFoundException fnf){
                System.out.println("Arquivo " + FULL_PATH + " nao encontrado.");
            } catch (IOException | ClassNotFoundException ex){
                System.out.println("Erro na leitura: " + ex.getMessage());
            }
        }
        return new ArrayList<>();
    }

    public static void writeHistory(List<String> currentHistory){
        File file = new File(FULL_PATH);
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))){
            outputStream.writeObject(currentHistory);
        } catch (IOException ex){
            System.out.println("Erro na gravacao: " + ex.getMessage());
        }
    }

    private static String getHomePath() {
        return System.getenv(isWindows() ? "HOMEPATH" : "HOME");
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
