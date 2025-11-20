package bankSockets;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String input;
            while ((input = in.readLine()) != null) {
                String resposta = processarComanda(input.trim());
                out.println(resposta);
            }

        } catch (IOException e) {
            System.out.println("Error amb client: " + e.getMessage());
        } finally {
            try {
                // Esperar 15 segons abans de tancar la connexió
                Thread.sleep(15000);
                socket.close();
            } catch (IOException e) {
                // Ignora
            }
            BankServerThread.desconnectarClient();
        }
    }

    private String processarComanda(String comanda) {
        System.out.println("Comanda: " + comanda);
        String[] parts = comanda.split(" ");
        if (parts.length == 0) return "300";

        String accio = parts[0];
        String resposta;

        switch (accio) {
            case "CREATE":
                resposta = (parts.length != 2) ? "301" : crearCompte(parts[1]);
                break;
            case "CREDIT":
                resposta = (parts.length != 3) ? "301" : ingressar(parts[1], parts[2]);
                break;
            case "DEBIT":
                resposta = (parts.length != 3) ? "301" : retirar(parts[1], parts[2]);
                break;
            case "TRANSFER":
                resposta = (parts.length != 4) ? "301" : transferir(parts[1], parts[2], parts[3]);
                break;
            case "DELETE":
                resposta = (parts.length != 2) ? "301" : eliminar(parts[1]);
                break;
            case "SHOW":
                resposta = (parts.length != 2) ? "301" : mostrar(parts[1]);
                break;
            default:
                resposta = "300";
                break;
        }

        System.out.println("Resposta: " + resposta);
        return resposta;
    }

    // Métodos sincronizados sobre el objeto comptes
    private String crearCompte(String id) {
        synchronized (BankServerThread.comptes) {
            if (!id.matches("\\d{8}")) return "200";
            if (BankServerThread.comptes.containsKey(id)) return "201";
            BankServerThread.comptes.put(id, 0.0);
            return "100";
        }
    }

    private String ingressar(String id, String qStr) {
        synchronized (BankServerThread.comptes) {
            if (!id.matches("\\d{8}")) return "200";
            if (!BankServerThread.comptes.containsKey(id)) return "202";
            try {
                double q = Double.parseDouble(qStr);
                BankServerThread.comptes.put(id, BankServerThread.comptes.get(id) + q);
                return "100";
            } catch (NumberFormatException e) {
                return "301";
            }
        }
    }

    private String retirar(String id, String qStr) {
        synchronized (BankServerThread.comptes) {
            if (!id.matches("\\d{8}")) return "200";
            if (!BankServerThread.comptes.containsKey(id)) return "202";
            try {
                double q = Double.parseDouble(qStr);
                double saldo = BankServerThread.comptes.get(id);
                if (saldo < q) return "203";
                BankServerThread.comptes.put(id, saldo - q);
                return "100";
            } catch (NumberFormatException e) {
                return "301";
            }
        }
    }

    private String transferir(String origen, String desti, String qStr) {
        synchronized (BankServerThread.comptes) {
            if (!origen.matches("\\d{8}") || !desti.matches("\\d{8}")) return "200";
            if (!BankServerThread.comptes.containsKey(origen) || !BankServerThread.comptes.containsKey(desti)) return "202";
            try {
                double q = Double.parseDouble(qStr);
                double saldoOrigen = BankServerThread.comptes.get(origen);
                if (saldoOrigen < q) return "203";
                BankServerThread.comptes.put(origen, saldoOrigen - q);
                BankServerThread.comptes.put(desti, BankServerThread.comptes.get(desti) + q);
                return "100";
            } catch (NumberFormatException e) {
                return "301";
            }
        }
    }

    private String eliminar(String id) {
        synchronized (BankServerThread.comptes) {
            if (!id.matches("\\d{8}")) return "200";
            if (!BankServerThread.comptes.containsKey(id)) return "202";
            BankServerThread.comptes.remove(id);
            return "100";
        }
    }

    private String mostrar(String id) {
        synchronized (BankServerThread.comptes) {
            if (!id.matches("\\d{8}")) return "200";
            if (!BankServerThread.comptes.containsKey(id)) return "202";
            return "100 Saldo: " + BankServerThread.comptes.get(id);
        }
    }
}