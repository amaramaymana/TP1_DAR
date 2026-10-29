package clientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        System.out.println("Je suis un client pas encore connecté…");
        try (Socket socket = new Socket("localhost", 1234);
             Scanner sc = new Scanner(System.in)) {
            System.out.println("je suis un client connecté");

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            int x;
            do {
                System.out.print("Entrez un entier (0 pour quitter) : ");
                x = sc.nextInt();
                out.writeInt(x);
                out.flush();
                int resultat = in.readInt();
                System.out.println(x + " * 5 = " + resultat);
            } while (x != 0);

            System.out.println("Fin : fermeture du socket client.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}