package bankSockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class BankServer {
    private static final int PORT = 5000;
    private static Map<String, Double> comptes = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor bancari actiu al port " + PORT);

        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client connectat: " + clientSocket.getInetAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String input;
                while ((input = in.readLine()) != null) {
                    String resposta = processarComanda(input.trim());
                    out.println(resposta);
                }
            } catch (IOException e) {
                System.out.println("Error amb el client: " + e.getMessage());
            }
        }
    }

    private static String processarComanda(String comanda) {
        String[] parts = comanda.split(" ");
        if (parts.length == 0) return "300";

        String accio = parts[0].toLowerCase();
        switch (accio) {
            case "create":
                if (parts.length != 2) return "301";
                return crearCompte(parts[1]);

            case "credit":
                if (parts.length != 3) return "301";
                return ingressar(parts[1], parts[2]);

            case "debit":
                if (parts.length != 3) return "301";
                return retirar(parts[1], parts[2]);

            case "transfer":
                if (parts.length != 4) return "301";
                return transferir(parts[1], parts[2], parts[3]);

            case "delete":
                if (parts.length != 2) return "301";
                return eliminar(parts[1]);

            case "show":
                if (parts.length != 2) return "301";
                return mostrar(parts[1]);

            default:
                return "300";
        }
    }

    private static boolean idValid(String id) {
        return id.matches("\\d{8}");
    }

    private static String crearCompte(String id) {
        if (!idValid(id)) return "200";
        if (comptes.containsKey(id)) return "201";
        comptes.put(id, 0.0);
        return "100";
    }

    private static String ingressar(String id, String quantitatStr) {
        if (!idValid(id)) return "200";
        if (!comptes.containsKey(id)) return "202";

        try {
            double q = Double.parseDouble(quantitatStr);
            comptes.put(id, comptes.get(id) + q);
            return "100";
        } catch (NumberFormatException e) {
            return "301";
        }
    }

    private static String retirar(String id, String quantitatStr) {
        if (!idValid(id)) return "200";
        if (!comptes.containsKey(id)) return "202";

        try {
            double q = Double.parseDouble(quantitatStr);
            double saldo = comptes.get(id);
            if (saldo < q) return "203";
            comptes.put(id, saldo - q);
            return "100";
        } catch (NumberFormatException e) {
            return "301";
        }
    }

    private static String transferir(String origen, String desti, String quantitatStr) {
        if (!idValid(origen) || !idValid(desti)) return "200";
        if (!comptes.containsKey(origen) || !comptes.containsKey(desti)) return "202";

        try {
            double q = Double.parseDouble(quantitatStr);
            double saldoOrigen = comptes.get(origen);
            if (saldoOrigen < q) return "203";
            comptes.put(origen, saldoOrigen - q);
            comptes.put(desti, comptes.get(desti) + q);
            return "100";
        } catch (NumberFormatException e) {
            return "301";
        }
    }

    private static String eliminar(String id) {
        if (!idValid(id)) return "200";
        if (!comptes.containsKey(id)) return "202";
        comptes.remove(id);
        return "100";
    }

    private static String mostrar(String id) {
        if (!idValid(id)) return "200";
        if (!comptes.containsKey(id)) return "202";
        return "100 Saldo: " + comptes.get(id);
    }
}
