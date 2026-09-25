import java.util.Random;

public class Tablero {
    private final int filas;
    private final int columnas;
    private final int totalMinas;
    private final boolean[][] minas;
    private final boolean[][] reveladas;
    private final boolean[][] marcadas;
    private final int[][] adyacentes;
    private boolean juegoTerminado = false;
    private boolean gano = false;

    public Tablero(int filas, int columnas, int totalMinas) {
        this.filas = filas;
        this.columnas = columnas;
        this.totalMinas = totalMinas;
        this.minas = new boolean[filas][columnas];
        this.reveladas = new boolean[filas][columnas];
        this.marcadas = new boolean[filas][columnas];
        this.adyacentes = new int[filas][columnas];
        colocarMinas();
        calcularAdyacentes();
    }

    private void colocarMinas() {
        Random rand = new Random();
        int colocadas = 0;
        while (colocadas < totalMinas) {
            int f = rand.nextInt(filas);
            int c = rand.nextInt(columnas);
            if (!minas[f][c]) {
                minas[f][c] = true;
                colocadas++;
            }
        }
    }

    private void calcularAdyacentes() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (minas[f][c]) continue;
                int contador = 0;
                for (int df = -1; df <= 1; df++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nf = f + df, nc = c + dc;
                        if (dentroDelTablero(nf, nc) && minas[nf][nc]) contador++;
                    }
                }
                adyacentes[f][c] = contador;
            }
        }
    }

    private boolean dentroDelTablero(int f, int c) {
        return f >= 0 && f < filas && c >= 0 && c < columnas;
    }

    public synchronized boolean revelar(int f, int c) {
        if (!dentroDelTablero(f, c) || reveladas[f][c] || marcadas[f][c] || juegoTerminado) return false;
        if (minas[f][c]) {
            reveladas[f][c] = true;
            juegoTerminado = true;
            gano = false;
            return true;
        }
        revelarEnCascada(f, c);
        if (verificarVictoria()) {
            juegoTerminado = true;
            gano = true;
        }
        return true;
    }

    private void revelarEnCascada(int f, int c) {
        if (!dentroDelTablero(f, c) || reveladas[f][c] || marcadas[f][c]) return;
        reveladas[f][c] = true;
        if (adyacentes[f][c] == 0) {
            for (int df = -1; df <= 1; df++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (df != 0 || dc != 0) revelarEnCascada(f + df, c + dc);
                }
            }
        }
    }

    public synchronized boolean marcar(int f, int c) {
        if (!dentroDelTablero(f, c) || reveladas[f][c] || juegoTerminado) return false;
        marcadas[f][c] = !marcadas[f][c];
        return true;
    }

    private boolean verificarVictoria() {
        for (int f = 0; f < filas; f++)
            for (int c = 0; c < columnas; c++)
                if (!minas[f][c] && !reveladas[f][c]) return false;
        return true;
    }

    public boolean isJuegoTerminado() { return juegoTerminado; }
    public boolean isGano() { return gano; }

    /** Vista que se le manda al jugador: oculta lo no revelado. */
    public String[][] obtenerVistaJugador() {
        String[][] vista = new String[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (juegoTerminado && minas[f][c]) {
                    vista[f][c] = "*";
                } else if (marcadas[f][c]) {
                    vista[f][c] = "P";
                } else if (!reveladas[f][c]) {
                    vista[f][c] = "-";
                } else {
                    vista[f][c] = String.valueOf(adyacentes[f][c]);
                }
            }
        }
        return vista;
    }
}