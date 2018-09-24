package com.ulbra.app.output;

import java.io.BufferedReader;
import java.io.UncheckedIOException;

public class Printer extends Thread {

    private BufferedReader output;

    public Printer(BufferedReader output){
        this.output = output;
    }

    @Override
    public void run() {
        try {
            output.lines().forEach(System.out::println);
        } catch (UncheckedIOException ex){
            System.out.println("DEBUG socket encerrado.");
        }
    }
}
