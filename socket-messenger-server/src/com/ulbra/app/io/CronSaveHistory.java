package com.ulbra.app.io;

import java.util.List;

public class CronSaveHistory extends Thread {

    private static final Long SLEEP_TIME_TO_WRITE = 20L;
    private List<String> messageHistory;

    public CronSaveHistory(List<String> messageHistory){
        this.messageHistory = messageHistory;
    }

    @Override
    public void run() {
        for (;;){
            try {
                MessageHistory.writeHistory(messageHistory);
                Thread.sleep(SLEEP_TIME_TO_WRITE * 1000L);
            } catch (InterruptedException ex){
                System.out.println("ERRO INTERNO: falha na gravacao do historico. " + ex.getMessage());
            }
        }
    }
}
