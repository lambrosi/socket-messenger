package com.ulbra.app.client;

import com.ulbra.app.output.Printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MessengerClient {

    public static void main(String[] args) {
        System.out.print("====================================\n");
        System.out.print("Informe o IP do servidor: ");
        String host = new Scanner(System.in).nextLine();

        System.out.print("Informe a porta: ");
        Integer port = new Scanner(System.in).nextInt();
        System.out.print("====================================\n\n");

        try (Socket s = new Socket(host, port);
             BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter output = new PrintWriter(s.getOutputStream(), true);
             Scanner consoleInput = new Scanner(System.in)){

            Printer printer = new Printer(input);
            printer.start();

            for(;;){
                String textToSend = consoleInput.nextLine();

                output.println(textToSend);
                output.flush();

                if(textToSend.toUpperCase().equals("SAIR")){
                    break;
                }
            }
        } catch (UnknownHostException ex) {
            System.out.println("Host desconhecido");
        } catch (IOException ex) {
            System.out.println("Erro na conexao: " + ex.getMessage());
        }
    }
}
