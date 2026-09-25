public class MensajeServidor {
    public String tipo = "ESTADO";
    public String[][] tablero;
    public String estado;   // "JUGANDO", "GANO" o "PERDIO"
    public String mensaje;

    public MensajeServidor(String[][] tablero, String estado, String mensaje) {
        this.tablero = tablero;
        this.estado = estado;
        this.mensaje = mensaje;
    }
}