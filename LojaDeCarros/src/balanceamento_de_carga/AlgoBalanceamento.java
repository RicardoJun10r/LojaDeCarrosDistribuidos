package balanceamento_de_carga;

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
    }

    private TipoBalanceamento validarAlgo(String tipo) {
        switch (tipo) {
            case "RR":
                return TipoBalanceamento.RR;
            case "LC":
                initFrequencia();
                return TipoBalanceamento.LC;
            case "WRR":
                this.servidor_index = 0;
                initFrequencia();
                return TipoBalanceamento.WRR;
            case "RA":
                this.random = new Random();
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
        this.frequencias[RR()]++;
        return menor(this.frequencias);
    }

    private int WRR() {
        if (this.servidor_index > this.quantidade_servidores) {
            this.servidor_index = 0;
            initFrequencia();
        }
        if (this.frequencias[this.servidor_index] < (this.servidor_index + 1))
            this.frequencias[this.servidor_index]++;
        else {
            this.servidor_index++;
            this.frequencias[this.servidor_index]++;
        }
        return this.servidor_index;
    }

    private int RA() {
        return this.random.nextInt(this.quantidade_servidores) - 1;
    }

    private int menor(int[] vet) {
        int __ = vet[0];
        for (int i = 0; i < vet.length; i++) {
            if (__ > vet[i])
                __ = vet[i];
        }
        return __;
    }

}
