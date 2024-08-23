package app.services;

import java.io.IOException;

import microsservice.server.LojaService;

public class Loja {

    public static void main(String[] args) {
        // 1
        LojaService lojaService = new LojaService(1060, 6156);
        // 2
        //LojaService lojaService = new LojaService(1061, 6157);
        // 3
        //LojaService lojaService = new LojaService(1062, 6158);
        try {
            lojaService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lojaService.shutdown();
    }

}
