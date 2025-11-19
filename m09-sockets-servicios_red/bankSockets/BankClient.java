import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BankClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Client bancari connectat.");

            while (true) {
                mostrarMenu();
                String input = sc.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) break;

                out.println(input);
                String resposta = in.readLine();
                mostrarResposta(resposta);
            }

        } catch (IOException e) {
            System.out.println("Error de connexió: " + e.getMessage());
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n--- MENÚ ---");
        System.out.println("CREATE <id_compte>");
        System.out.println("CREDIT <id_compte> <quantitat>");
        System.out.println("DEBIT <id_compte> <quantitat>");
        System.out.println("TRANSFER <origen> <desti> <quantitat>");
        System.out.println("DELETE <id_compte>");
        System.out.println("SHOW <id_compte>");
        System.out.println("exit");
        System.out.print("Entra una ordre: ");
    }

    private static void mostrarResposta(String resposta) {
        if (resposta == null) return;
        if (resposta.startsWith("100")) {
            if (resposta.length() > 3) {
                System.out.println("" + resposta.substring(4));
            } else {
                System.out.println("Operació realitzada correctament.");
            }
        } else {
            switch (resposta) {
                case "200": System.out.println("Error: ID incorrecte (han de ser 8 dígits)."); break;
                case "201": System.out.println("Error: El compte ja existeix."); break;
                case "202": System.out.println("Error: El compte no existeix."); break;
                case "203": System.out.println("Error: Saldo insuficient."); break;
                case "300": System.out.println("Error: Ordre no reconeguda."); break;
                case "301": System.out.println("Error: Paràmetres incorrectes."); break;
                default: System.out.println("Resposta del servidor: " + resposta); break;
            }
        }
    }
}