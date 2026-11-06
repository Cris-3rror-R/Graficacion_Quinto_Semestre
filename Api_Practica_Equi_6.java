import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Api_Practica_Equi_6 extends JPanel implements ActionListener, KeyListener {

    // --- Clase interna para representar puntos 3D ---
    static class Point3D {
        double x, y, z;
        Point3D(double x, double y, double z) {
            this.x = x; this.y = y; this.z = z;
        }
    }

    // --- Variables del cubo ---
    private final Point3D[] vertices;
    private final int[][] aristas;
    private double anguloX = 0, anguloY = 0;
    private boolean mostrarCubo = true; // alterna entre cubo y esfera

    // --- Timer para animación ---
    private final Timer timer;

    public Api_Practica_Equi_6() {
        // --- Definir los vértices del cubo ---
        vertices = new Point3D[]{
                new Point3D(-100, -100, -100),
                new Point3D(100, -100, -100),
                new Point3D(100, 100, -100),
                new Point3D(-100, 100, -100),
                new Point3D(-100, -100, 100),
                new Point3D(100, -100, 100),
                new Point3D(100, 100, 100),
                new Point3D(-100, 100, 100)
        };

        // --- Definir las aristas ---
        aristas = new int[][]{
                {0, 1}, {1, 2}, {2, 3}, {3, 0}, // base trasera
                {4, 5}, {5, 6}, {6, 7}, {7, 4}, // base delantera
                {0, 4}, {1, 5}, {2, 6}, {3, 7}  // uniones
        };

        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // --- Iniciar animación ---
        timer = new Timer(50, this); // cada 50 ms
        timer.start();
    }

    // --- Rotación en 3D alrededor de X y Y ---
    private Point3D rotar(Point3D p, double angX, double angY) {
        double cosX = Math.cos(angX), sinX = Math.sin(angX);
        double cosY = Math.cos(angY), sinY = Math.sin(angY);

        double y = p.y * cosX - p.z * sinX;
        double z = p.y * sinX + p.z * cosX;
        double x = p.x * cosY + z * sinY;
        z = -p.x * sinY + z * cosY;

        return new Point3D(x, y, z);
    }

    // --- Proyección de 3D a 2D ---
    private Point proyectar(Point3D p, int width, int height) {
        double distancia = 400;
        double factor = distancia / (distancia + p.z);
        int x2d = (int) (p.x * factor + width / 2);
        int y2d = (int) (-p.y * factor + height / 2);
        return new Point(x2d, y2d);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        if (mostrarCubo) {
            g2.setColor(Color.WHITE);
            // Dibujar cubo
            for (int[] a : aristas) {
                Point3D p1 = rotar(vertices[a[0]], anguloX, anguloY);
                Point3D p2 = rotar(vertices[a[1]], anguloX, anguloY);
                Point punto1 = proyectar(p1, w, h);
                Point punto2 = proyectar(p2, w, h);
                g2.drawLine(punto1.x, punto1.y, punto2.x, punto2.y);
            }
            g2.setColor(Color.GREEN);
            g2.drawString("Mostrando: Cubo (presiona ESPACIO para cambiar)", 10, 20);
        } else {
            // Dibujar esfera
            g2.setColor(Color.CYAN);
            double r = 150;
            double distancia = 400;
            for (double phi = 0; phi <= Math.PI; phi += Math.PI / 18) {
                for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 18) {
                    double x = r * Math.cos(theta) * Math.sin(phi);
                    double y = r * Math.sin(theta) * Math.sin(phi);
                    double z = r * Math.cos(phi);

                    // Rotar un poco también la esfera
                    Point3D p = rotar(new Point3D(x, y, z), anguloX / 2, anguloY / 2);

                    double factor = distancia / (distancia + p.z);
                    int x2d = (int) (p.x * factor + w / 2);
                    int y2d = (int) (-p.y * factor + h / 2);
                    g2.fillOval(x2d, y2d, 2, 2);
                }
            }
            g2.setColor(Color.GREEN);
            g2.drawString("Mostrando: Esfera (presiona ESPACIO para cambiar)", 10, 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        anguloX += 0.05;
        anguloY += 0.03;
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            mostrarCubo = !mostrarCubo; // cambiar entre cubo y esfera
            repaint();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame f = new JFrame("Dibujo 3D - Cubo y Esfera");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new Api_Practica_Equi_6());
        f.setSize(700, 700);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}