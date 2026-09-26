import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente TCP igual al de la sesión pasada, pero jugando Buscaminas
 * en vez de mandar eco. Persona B.
 */
public class BuscaminasClient {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 9000;
        Gson gson = new Gson();

        try (Socket socket = new Socket(host, puerto);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Conectado al servidor de Buscaminas");
            mostrarTablero(gson, in.readLine());

            while (true) {
                System.out.print("Acción (revelar/marcar/salir): ");
                String accion = sc.nextLine().trim().toLowerCase();
                if (accion.equals("salir")) {
                    out.println(gson.toJson(crearMensaje("SALIR", 0, 0)));
                    break;
                }

                System.out.print("Fila: ");
                int fila = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Columna: ");
                int columna = Integer.parseInt(sc.nextLine().trim());

                String tipo = accion.equals("marcar") ? "MARCAR" : "REVELAR";
                out.println(gson.toJson(crearMensaje(tipo, fila, columna)));

                String respuesta = in.readLine();
                if (respuesta == null) break;
                MensajeServidor estado = mostrarTablero(gson, respuesta);
                if (!"JUGANDO".equals(estado.estado)) {
                    System.out.println(estado.mensaje);
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    private static MensajeCliente crearMensaje(String tipo, int fila, int columna) {
        MensajeCliente m = new MensajeCliente();
        m.tipo = tipo;
        m.fila = fila;
        m.columna = columna;
        return m;
    }

    private static MensajeServidor mostrarTablero(Gson gson, String json) {
        MensajeServidor estado = gson.fromJson(json, MensajeServidor.class);
        System.out.println();
        for (String[] fila : estado.tablero) {
            for (String celda : fila) {
                System.out.print(celda + " ");
            }
            System.out.println();
        }
        System.out.println("Estado: " + estado.estado + " | " + estado.mensaje);
        return estado;
    }
}