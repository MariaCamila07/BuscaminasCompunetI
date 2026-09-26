import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BuscaminasServer {
    private static final int PUERTO = 9000;
    private static final ExecutorService pool = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor de Buscaminas escuchando en puerto " + PUERTO);
            while (true) {
                Socket clienteSocket = servidor.accept();
                System.out.println("Nuevo jugador: " + clienteSocket.getRemoteSocketAddress());
                pool.execute(new ManejadorCliente(clienteSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}