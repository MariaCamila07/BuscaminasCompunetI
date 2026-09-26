import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BuscaminasClient {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 9000;

        try (Socket socket = new Socket(host, puerto);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Conectado al servidor de Buscaminas");
            String estado = mostrarTablero(in);

            while (estado.equals("JUGANDO")) {
                System.out.print("Acción (revelar/marcar/salir): ");
                String accion = sc.nextLine().trim().toLowerCase();
                if (accion.equals("salir")) {
                    out.println("SALIR");
                    break;
                }

                System.out.print("Fila: ");
                int fila = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Columna: ");
                int columna = Integer.parseInt(sc.nextLine().trim());

                String comando = (accion.equals("marcar") ? "MARCAR" : "REVELAR") + " " + fila + " " + columna;
                out.println(comando);

                estado = mostrarTablero(in);
            }
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    private static String mostrarTablero(BufferedReader in) throws IOException {
        String tableroTexto = in.readLine();
        String estado = in.readLine();
        String mensaje = in.readLine();

        System.out.println();
        String[] filas = tableroTexto.split(";");
        for (String fila : filas) {
            String[] celdas = fila.split(",");
            for (String celda : celdas) {
                System.out.print(celda + " ");
            }
            System.out.println();
        }
        System.out.println("Estado: " + estado + " | " + mensaje);
        return estado;
    }
}