import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Un ClientHandler igual al de la sesión pasada, pero en vez de hacer eco
 * mantiene una partida de Buscaminas completa para ese cliente. Persona B.
 */
public class ManejadorCliente implements Runnable {
    private static final int FILAS = 5;
    private static final int COLUMNAS = 5;
    private static final int MINAS = 4;

    private final Socket socket;
    private final Gson gson = new Gson();

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (Socket s = this.socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {

            Tablero tablero = new Tablero(FILAS, COLUMNAS, MINAS);
            enviarEstado(out, tablero, "JUGANDO", "Partida iniciada");

            String linea;
            while ((linea = in.readLine()) != null) {
                MensajeCliente msg = gson.fromJson(linea, MensajeCliente.class);
                if (msg == null || "SALIR".equalsIgnoreCase(msg.tipo)) break;

                if (tablero.isJuegoTerminado()) {
                    enviarEstado(out, tablero, tablero.isGano() ? "GANO" : "PERDIO", "La partida ya terminó");
                    continue;
                }

                if ("REVELAR".equalsIgnoreCase(msg.tipo)) {
                    tablero.revelar(msg.fila, msg.columna);
                } else if ("MARCAR".equalsIgnoreCase(msg.tipo)) {
                    tablero.marcar(msg.fila, msg.columna);
                }

                String estado = tablero.isJuegoTerminado() ? (tablero.isGano() ? "GANO" : "PERDIO") : "JUGANDO";
                String mensaje = estado.equals("GANO") ? "¡Ganaste!"
                        : estado.equals("PERDIO") ? "Pisaste una mina"
                        : "Ok";
                enviarEstado(out, tablero, estado, mensaje);

                if (!estado.equals("JUGANDO")) break;
            }
        } catch (IOException e) {
            System.err.println("Error con cliente: " + e.getMessage());
        }
    }

    private void enviarEstado(PrintWriter out, Tablero tablero, String estado, String mensaje) {
        MensajeServidor respuesta = new MensajeServidor(tablero.obtenerVistaJugador(), estado, mensaje);
        out.println(gson.toJson(respuesta));
    }
}