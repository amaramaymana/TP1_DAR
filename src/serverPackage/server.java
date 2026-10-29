package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;

public class server {

    static final int PORT = 1234;

    static InetAddress trouverIpLocale() throws IOException {
        for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
            for (InetAddress a : Collections.list(ni.getInetAddresses())) {
                if (a instanceof Inet4Address && a.isSiteLocalAddress()) return a;
            }
        }
        return InetAddress.getLocalHost();
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            InetAddress ip = (args.length > 0) ? InetAddress.getByName(args[0]) : trouverIpLocale();
            serverSocket.bind(new InetSocketAddress(ip, PORT));
            System.out.println("Serveur en attente sur " + ip.getHostAddress() + ":" + PORT);

            try (Socket socket = serverSocket.accept()) {
                System.out.println("Client connecté : " + socket.getInetAddress().getHostAddress());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    int op = in.readInt();
                    if (op == 0) break;
                    double a = in.readDouble();
                    double b = in.readDouble();

                    switch (op) {
                        case 1: envoyer(out, a + b); break;
                        case 2: envoyer(out, a - b); break;
                        case 3: envoyer(out, a * b); break;
                        case 4:
                            if (b == 0) erreur(out, "Division par zéro impossible");
                            else envoyer(out, a / b);
                            break;
                        default: erreur(out, "Opération inconnue : " + op);
                    }
                    System.out.println("op=" + op + " a=" + a + " b=" + b);
                }
                System.out.println("Fin de la communication.");
            }
        } catch (EOFException e) {
            System.out.println("Le client s'est déconnecté.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void envoyer(DataOutputStream out, double r) throws IOException {
        out.writeBoolean(true);
        out.writeDouble(r);
        out.flush();
    }

    static void erreur(DataOutputStream out, String msg) throws IOException {
        out.writeBoolean(false);
        out.writeUTF(msg);
        out.flush();
    }
}