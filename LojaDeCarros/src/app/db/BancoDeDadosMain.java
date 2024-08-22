package app.db;

import java.io.IOException;

import db.BancoDeDados;

public class BancoDeDadosMain {
    public static void main(String[] args) {
        // 1
        BancoDeDados bancoDeDados = new BancoDeDados(6156, 6157, 6158, 1060, 1050);
        // 2
        //BancoDeDados bancoDeDados = new BancoDeDados(6157, 6156, 6158, 1061, 1050);
        // 3
        //BancoDeDados bancoDeDados = new BancoDeDados(6158, 6156, 6157, 1062, 1050);
        try {
            bancoDeDados.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
