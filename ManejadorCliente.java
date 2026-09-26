import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ManejadorCliente implements Runnable {
    private static final int FILAS = 5;
    private static final int COLUMNAS = 5;
    private static final int MINAS = 4;

    private final Socket socket;

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
                linea = linea.trim();
                if (linea.equalsIgnoreCase("SALIR")) break;

                if (tablero.isJuegoTerminado()) {
                    enviarEstado(out, tablero, tablero.isGano() ? "GANO" : "PERDIO", "La partida ya terminó");
                    continue;
                }

                String[] partes = linea.split("\\s+");
                if (partes.length < 3) {
                    enviarEstado(out, tablero, "JUGANDO", "Comando inválido, usa: REVELAR fila columna");
                    continue;
                }

                String accion = partes[0];
                int fila = Integer.parseInt(partes[1]);
                int columna = Integer.parseInt(partes[2]);

                if (accion.equalsIgnoreCase("REVELAR")) {
                    tablero.revelar(fila, columna);
                } else if (accion.equalsIgnoreCase("MARCAR")) {
                    tablero.marcar(fila, columna);
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
        out.println(tablero.obtenerVistaComoTexto());
        out.println(estado);
        out.println(mensaje);
    }
}