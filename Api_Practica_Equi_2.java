import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Api_Practica_Equi_2 {

    public static void main(String[] args) {
        JFrame Principal = new JFrame();
        Principal.setSize(750, 500);
        Principal.setTitle("Practica 2 Equipo 2 - Trazado de Curvas de Bézier N=4");
        Principal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Principal.setLocationRelativeTo(null);
        Principal.setResizable(false);

        JPanel PPrincipal = new JPanel();
        PPrincipal.setLayout(new GridLayout(2, 1, 2, 2));

        PLienzo lienzo = new PLienzo(); // Panel de dibujo
        JPanel POpciones = new JPanel();
        POpciones.setLayout(new GridLayout(1, 2, 2, 2));
        POpciones.setBackground(Color.GRAY.brighter());

        JButton BLimpiar = new JButton("Limpiar");
        BLimpiar.addActionListener(e -> lienzo.limpiar());

        JButton BSalir = new JButton("Salir");
        BSalir.addActionListener(e -> System.exit(0));

        POpciones.add(BLimpiar);
        POpciones.add(BSalir);

        PPrincipal.add(lienzo);
        PPrincipal.add(POpciones);
        Principal.add(PPrincipal);
        Principal.setVisible(true);
    }
}

class PLienzo extends JPanel {
    private ArrayList<Point> puntos = new ArrayList<>();
    private Point puntoSeleccionado = null;
    private final int radio = 10;

    public PLienzo() {
        setBackground(Color.WHITE);

        // Clic para agregar o seleccionar punto
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point click = e.getPoint();
                for (Point p : puntos) {
                    if (click.distance(p) < radio) {
                        puntoSeleccionado = p;
                        return;
                    }
                }
                puntos.add(click);
                repaint();
            }

            public void mouseReleased(MouseEvent e) {
                puntoSeleccionado = null;
            }
        });

        // Arrastrar punto para moverlo
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (puntoSeleccionado != null) {
                    puntoSeleccionado.setLocation(e.getPoint());
                    repaint();
                }
            }
        });
    }

    public void limpiar() {
        puntos.clear();
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Dibujar puntos
        g2.setColor(Color.BLUE);
        for (Point p : puntos) {
            g2.fillOval(p.x - radio / 2, p.y - radio / 2, radio, radio);
        }

        // Dibujar múltiples curvas de Bézier cúbicas
        g2.setColor(Color.RED);
        for (int i = 0; i + 3 < puntos.size(); i += 3) {
            Point anterior = calcularBezier(puntos.get(i), puntos.get(i + 1), puntos.get(i + 2), puntos.get(i + 3), 0);
            for (double t = 0.01; t <= 1.0; t += 0.01) {
                Point actual = calcularBezier(puntos.get(i), puntos.get(i + 1), puntos.get(i + 2), puntos.get(i + 3), t);
                g2.drawLine(anterior.x, anterior.y, actual.x, actual.y);
                anterior = actual;
            }
        }
    }

    // Fórmula de Bézier cúbica para 4 puntos dados
    private Point calcularBezier(Point P0, Point P1, Point P2, Point P3, double t) {
        double x = Math.pow(1 - t, 3) * P0.x +
                   3 * Math.pow(1 - t, 2) * t * P1.x +
                   3 * (1 - t) * Math.pow(t, 2) * P2.x +
                   Math.pow(t, 3) * P3.x;

        double y = Math.pow(1 - t, 3) * P0.y +
                   3 * Math.pow(1 - t, 2) * t * P1.y +
                   3 * (1 - t) * Math.pow(t, 2) * P2.y +
                   Math.pow(t, 3) * P3.y;

        return new Point((int) x, (int) y);
    }
}
