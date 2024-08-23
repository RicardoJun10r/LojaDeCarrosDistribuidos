package balanceamento_de_carga;

import java.util.Arrays;
import java.util.Random;

import util.TipoBalanceamento;

public class AlgoBalanceamento {

    private TipoBalanceamento tipo;

    private int quantidade_servidores;

    private int servidor_index;

    private int[] frequencias;

    private Random random;

    public AlgoBalanceamento(String tipo, int servidores) {
        this.tipo = validarAlgo(tipo);
        this.quantidade_servidores = servidores;
        this.servidor_index = -1;
        this.servidor_index = 0;
        initFrequencia();
        this.random = new Random();
    }

    private TipoBalanceamento validarAlgo(String tipo) {
        switch (tipo) {
            case "RR":
                return TipoBalanceamento.RR;
            case "LC":
                return TipoBalanceamento.LC;
            case "WRR":
                return TipoBalanceamento.WRR;
            case "RA":
                return TipoBalanceamento.RA;
            default:
                return TipoBalanceamento.RR;
        }
    }

    public int algo() {
        switch (this.tipo) {
            case RR:
                return RR();
            case LC:
                return LC();
            case WRR:
                return WRR();
            case RA:
                return RA();
            default:
                return RR();
        }
    }

    private void initFrequencia() {
        this.frequencias = new int[this.quantidade_servidores];
        for (int i = 0; i < this.frequencias.length; i++) {
            frequencias[i] = 0;
        }
    }

    private int RR() {
        this.servidor_index = (this.servidor_index + 1) % this.quantidade_servidores;
        return this.servidor_index;
    }

    private int LC() {
        int index = RA();
        this.frequencias[index]++;
        return menor();
    }

    private int WRR() {
        if (this.servidor_index == this.quantidade_servidores) {
            System.out.println("reiniciando vetor");
            this.servidor_index = 0;
            initFrequencia();
        }
        if (this.frequencias[this.servidor_index] < (this.servidor_index + 1)){
            System.out.println("aumentando frequencia");
            this.frequencias[this.servidor_index]++;
        }
        else {
            System.out.println("somando index");
            this.servidor_index++;
            this.frequencias[this.servidor_index]++;
        }
        System.out.println("SERVIDOR_INDEX: " + this.servidor_index);
        return this.servidor_index;
    }

    private int RA() {
        return this.random.nextInt(this.quantidade_servidores);
    }

    private int menor() {
        int index = 0;
        for (int i = 1; i < this.frequencias.length; i++) {
            if (this.frequencias[i] < this.frequencias[index]) {
                index = i;
            }
        }
        System.out.println(Arrays.toString(this.frequencias));
        return index;
    }

}
