package app.firewall;

import java.io.IOException;

import firewall.Firewall;

public class FirewallMain {
    public static void main(String[] args) {
        Firewall firewall = new Firewall("RA");
        try {
            firewall.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        firewall.shutdown();
    }
}
