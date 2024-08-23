package app.gateway;

import java.io.IOException;

import microsservice.gateway.Gateway;

public class GatewayMain {
    public static void main(String[] args) {
        // 1
        Gateway gateway = new Gateway(1042);
        // 2
        //Gateway gateway = new Gateway(1043);
        try {
            gateway.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gateway.shutdown();
    }
}
