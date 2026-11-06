
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;

// CLASE PRINCIPAL DEL PANEL DE DIBUJO 3D
public class Api_Practica_Equi_8 extends JPanel implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener {

    // Clase para representar puntos 3D (Sin cambios)
    static class Point3D {

        double x, y, z;

        Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Point3D restar(Point3D p) {
            return new Point3D(x - p.x, y - p.y, z - p.z);
        }

        double productoPunto(Point3D p) {
            return x * p.x + y * p.y + z * p.z;
        }

        Point3D productoVectorial(Point3D p) {
            return new Point3D(y * p.z - z * p.y, z * p.x - x * p.z, x * p.y - y * p.x);
        }

        double magnitud() {
            return Math.sqrt(x * x + y * y + z * z);
        }

        Point3D normalizar() {
            double mag = magnitud();
            if (mag == 0) {
                return new Point3D(0, 0, 0);
            }
            return new Point3D(x / mag, y / mag, z / mag);
        }
    }

    // Clase para representar una cara del objeto (Cubo o Esfera)
    static class Cara {

        int[] indices;
        Color color;

        Cara(int[] indices) {
            this.indices = indices;
        }
    }

    // Variables de control de iluminación (sin cambios)
    private double intensidadLuz = 1.0;
    private double factorSombraBase = 0.3;
    private static final double LUZ_AMBIENTAL = 0.05;

    // Variables del cubo y control
    private final Point3D[] verticesBase;
    private double anguloX = 0, anguloY = 0, anguloZ = 0;
    private boolean mostrarCubo = true;

    // --- ESFERA Y CUBO MESH DATA ---
    private final Point3D[] verticesEsferaBase;
    private final Cara[] carasEsfera;

    private Point3D fuenteLuz = new Point3D(300, -200, 500);
    private final Point3D observador = new Point3D(0, 0, 800);

    // Control del mouse
    private int mouseX, mouseY;
    private boolean rotando = false;
    private boolean moviendoLuz = false;

    private final double COEFICIENTE_DIFUSO = 0.7;
    private final double COEFICIENTE_ESPECULAR = 0.3;
    private final int EXPONENTE_ESPECULAR = 20;

    private final Timer timer;

    // Caras del cubo (sin cambios)
    private final Cara[] carasCubo = new Cara[]{
        new Cara(new int[]{0, 1, 2, 3}), new Cara(new int[]{4, 5, 6, 7}),
        new Cara(new int[]{0, 4, 7, 3}), new Cara(new int[]{1, 5, 6, 2}),
        new Cara(new int[]{0, 1, 5, 4}), new Cara(new int[]{3, 2, 6, 7})
    };

    public Api_Practica_Equi_8() {
        // Vértices base del cubo
        verticesBase = new Point3D[]{
            new Point3D(-100, -100, -100), new Point3D(100, -100, -100), new Point3D(100, 100, -100), new Point3D(-100, 100, -100),
            new Point3D(-100, -100, 100), new Point3D(100, -100, 100), new Point3D(100, 100, 100), new Point3D(-100, 100, 100)
        };
        // Colores del cubo
        carasCubo[0].color = new Color(200, 0, 0);
        carasCubo[1].color = new Color(0, 200, 0);
        carasCubo[2].color = new Color(0, 0, 200);
        carasCubo[3].color = new Color(200, 200, 0);
        carasCubo[4].color = new Color(200, 0, 200);
        carasCubo[5].color = new Color(0, 200, 200);

        // --- GENERACIÓN DE LA ESFERA COMO MALLA (MESH) ---
        List<Point3D> vertices = new ArrayList<>();
        List<Cara> caras = new ArrayList<>();
        generarEsferaMesh(150, 20, 20, vertices, caras);
        this.verticesEsferaBase = vertices.toArray(new Point3D[0]);
        this.carasEsfera = caras.toArray(new Cara[0]);
        for (Cara cara : carasEsfera) {
            cara.color = Color.CYAN;
        }
        // --------------------------------------------------

        setBackground(Color.BLACK);
        setFocusable(true);

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        timer = new Timer(50, this);
    }

    /*Genera los vértices y las caras de una esfera parametrizada*/
    private void generarEsferaMesh(double radio, int stacks, int slices, List<Point3D> vertices, List<Cara> caras) {
        // Vértices
        for (int i = 0; i <= stacks; i++) {
            double phi = Math.PI * i / stacks; // angulo vertical (phi)
            for (int j = 0; j <= slices; j++) {
                double theta = 2 * Math.PI * j / slices; // angulo horizontal (theta)

                double x = radio * Math.sin(phi) * Math.cos(theta);
                double y = radio * Math.sin(phi) * Math.sin(theta);
                double z = radio * Math.cos(phi);

                vertices.add(new Point3D(x, y, z));
            }
        }

        // Caras (cuadriláteros)
        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < slices; j++) {
                int p1 = i * (slices + 1) + j;
                int p2 = i * (slices + 1) + j + 1;
                int p3 = (i + 1) * (slices + 1) + j + 1;
                int p4 = (i + 1) * (slices + 1) + j;

                // Crear la cara (p4, p3, p2, p1 para mantener la orientación correcta)
                caras.add(new Cara(new int[]{p4, p3, p2, p1}));
            }
        }
    }

    public void setIntensidadLuz(double intensidad) {
        this.intensidadLuz = intensidad;
        repaint();
    }

    public void setFactorSombra(double factor) {
        this.factorSombraBase = factor;
        repaint();
    }

    // Métodos de Geometría y Proyección (sin cambios)
    private Point3D rotar(Point3D p, double angX, double angY, double angZ) {
        double cosX = Math.cos(angX), sinX = Math.sin(angX);
        double y1 = p.y * cosX - p.z * sinX;
        double z1 = p.y * sinX + p.z * cosX;

        double cosY = Math.cos(angY), sinY = Math.sin(angY);
        double x2 = p.x * cosY + z1 * sinY;
        double z2 = -p.x * sinY + z1 * cosY;

        double cosZ = Math.cos(angZ), sinZ = Math.sin(angZ);
        double x3 = x2 * cosZ - y1 * sinZ;
        double y3 = x2 * sinZ + y1 * cosZ;

        return new Point3D(x3, y3, z2);
    }

    private Point proyectar(Point3D p, int width, int height) {
        double distancia = 400.0;
        double denom = distancia + p.z;
        double factor = denom == 0 ? 0.0001 : distancia / denom;
        int x2d = (int) (p.x * factor + width / 2.0);
        int y2d = (int) (-p.y * factor + height / 2.0);
        return new Point(x2d, y2d);
    }

    private Color calcularColorConIluminacion(Color colorBase, Point3D normal, Point3D posicionCara, boolean enSombra) {
        Point3D L = fuenteLuz.restar(posicionCara).normalizar();
        Point3D V = observador.restar(posicionCara).normalizar();

        double ndotl = Math.max(normal.productoPunto(L), 0);

        double difusa = ndotl * COEFICIENTE_DIFUSO * intensidadLuz;
        double especular = Math.pow(Math.max(L.restar(normal).normalizar().productoPunto(V), 0), EXPONENTE_ESPECULAR) * COEFICIENTE_ESPECULAR * intensidadLuz;

        double intensidad = difusa + especular;

        if (enSombra) {
            intensidad = LUZ_AMBIENTAL + (intensidad * factorSombraBase);
        }

        intensidad = Math.min(intensidad, 1.0);

        int r = (int) (colorBase.getRed() * intensidad);
        int g = (int) (colorBase.getGreen() * intensidad);
        int b = (int) (colorBase.getBlue() * intensidad);

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return new Color(r, g, b);
    }

    private Point3D calcularCentroCaraRotada(Cara cara, Point3D[] vRot) {
        /* ... */
        double x = 0, y = 0, z = 0;
        for (int i : cara.indices) {
            x += vRot[i].x;
            y += vRot[i].y;
            z += vRot[i].z;
        }
        int count = cara.indices.length;
        return new Point3D(x / count, y / count, z / count);
    }

    private Point3D calcularNormalConVerticesRotados(Cara cara, Point3D[] vRot) {
        /* ... */
        Point3D p1 = vRot[cara.indices[0]];
        Point3D p2 = vRot[cara.indices[1]];
        Point3D p3 = vRot[cara.indices[2]];
        Point3D v1 = p2.restar(p1);
        Point3D v2 = p3.restar(p1);
        return v1.productoVectorial(v2).normalizar();
    }

    // La función de sombra solo se aplica al cubo (que es convexo y simple)
    private boolean estaEnSombra(Cara caraActual, Point3D centroCara, Point3D[] vRot) {
        if (!mostrarCubo) {
            return false; // La sombra del cubo es más simple de calcular
        }
        Point3D dirLuz = fuenteLuz.restar(centroCara).normalizar();

        for (Cara caraSombra : carasCubo) { // Solo verificamos contra las caras del cubo
            if (caraSombra == caraActual) {
                continue;
            }

            Point3D centroSombra = calcularCentroCaraRotada(caraSombra, vRot);
            Point3D normalSombra = calcularNormalConVerticesRotados(caraSombra, vRot);

            if (normalSombra.productoPunto(dirLuz) < -0.01) {
                Point3D aCaraSombra = centroSombra.restar(centroCara);

                if (aCaraSombra.productoPunto(dirLuz) > 1.0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Point3D[] vBase;
        Cara[] carasDibujo;

        if (mostrarCubo) {
            vBase = verticesBase;
            carasDibujo = carasCubo;
        } else {
            vBase = verticesEsferaBase;
            carasDibujo = carasEsfera;
        }

        // 1. Rotación de vértices
        Point3D[] verticesRotados = new Point3D[vBase.length];
        for (int i = 0; i < vBase.length; i++) {
            // La esfera rota más lento en la animación que el cubo
            if (mostrarCubo) {
                verticesRotados[i] = rotar(vBase[i], anguloX, anguloY, anguloZ);
            } else {
                verticesRotados[i] = rotar(vBase[i], anguloX / 2, anguloY / 2, anguloZ / 2);
            }
        }

        // 2. Lógica de renderizado
        class FaceDraw {

            Cara cara;
            Polygon poly;
            Color color;
            double zProm;
        }
        List<FaceDraw> drawList = new ArrayList<>();

        for (Cara cara : carasDibujo) {
            Point3D normal = calcularNormalConVerticesRotados(cara, verticesRotados);
            Point3D centro = calcularCentroCaraRotada(cara, verticesRotados);

            // Para la esfera, la normal de la cara es muy aproximada.
            // Es más preciso usar la normal del centro (vector desde el origen)
            // Ya que es una esfera.
            Point3D normalRender = mostrarCubo ? normal : centro.normalizar();

            Point3D toCamera = observador.restar(centro).normalizar();
            double facing = normalRender.productoPunto(toCamera);
            if (facing <= 0) {
                continue; // Backface culling
            }
            boolean enSombra = estaEnSombra(cara, centro, verticesRotados);

            Color colorIluminado = calcularColorConIluminacion(cara.color, normalRender, centro, enSombra);

            Polygon poligono = new Polygon();
            double zAccum = 0;
            for (int idx : cara.indices) {
                Point punto2D = proyectar(verticesRotados[idx], w, h);
                poligono.addPoint(punto2D.x, punto2D.y);
                zAccum += verticesRotados[idx].z;
            }

            FaceDraw fd = new FaceDraw();
            fd.cara = cara;
            fd.poly = poligono;
            fd.color = colorIluminado;
            fd.zProm = zAccum / cara.indices.length;
            drawList.add(fd);
        }

        // 3. Orden por profundidad (Painter's Algorithm)
        drawList.sort(Comparator.comparingDouble(fd -> fd.zProm));

        for (FaceDraw fd : drawList) {
            g2.setColor(fd.color);
            g2.fillPolygon(fd.poly);
            g2.setColor(Color.WHITE);
            // Dibujar el borde de los polígonos solo para el cubo
            if (mostrarCubo) {
                g2.drawPolygon(fd.poly);
            }
        }

        g2.setColor(Color.WHITE);
        String objeto = mostrarCubo ? "Cubo Sólido" : "Esfera Sólida (Malla)";
        g2.drawString("Mostrando: " + objeto + ". Int. Luz: " + String.format("%.2f", intensidadLuz), 10, 20);
        g2.drawString("Factor Sombra: " + String.format("%.2f", factorSombraBase), 10, 40);
        g2.drawString("Rotación: Clic Izq + Arrastrar | Mover Luz (X/Y): Clic Der + Arrastrar", 10, 60);
        g2.drawString("Mover Luz (Z): Ctrl + Scroll | Pos Luz: X=" + String.format("%.0f", fuenteLuz.x) + ", Y=" + String.format("%.0f", fuenteLuz.y) + ", Z=" + String.format("%.0f", fuenteLuz.z), 10, 80);

        // Dibujar la fuente de luz
        Point luzProyectada = proyectar(fuenteLuz, w, h);
        g2.setColor(Color.YELLOW);
        g2.fillOval(luzProyectada.x - 5, luzProyectada.y - 5, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawString("Fuente de Luz", luzProyectada.x + 10, luzProyectada.y);
    }

    // Métodos de Listener (sin cambios en la lógica de control)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!mostrarCubo) {
            anguloX += 0.02;
            anguloY += 0.03;
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if (e.getButton() == MouseEvent.BUTTON1) {
            rotando = true;
            moviendoLuz = false;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            if (e.isControlDown()) {
                mostrarCubo = !mostrarCubo;
                if (mostrarCubo) {
                    timer.stop();
                } else {
                    timer.start();
                }
            } else {
                moviendoLuz = true;
                rotando = false;
            }
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        rotando = false;
        moviendoLuz = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int deltaX = e.getX() - mouseX;
        int deltaY = e.getY() - mouseY;

        if (moviendoLuz) {
            fuenteLuz.x += deltaX * 2.0;
            fuenteLuz.y -= deltaY * 2.0;
        } else if (rotando) {
            anguloY += deltaX * 0.01;
            anguloX += deltaY * 0.01;
        }

        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            fuenteLuz.z -= e.getWheelRotation() * 50.0;
        } else {
            anguloZ += e.getWheelRotation() * 0.1;
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3 && !e.isControlDown()) {
            mostrarCubo = !mostrarCubo;
            if (mostrarCubo) {
                timer.stop();
            } else {
                timer.start();
            }
            repaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    // CLASE MAIN PARA CREAR LA VENTANA Y SLIDERS (Sin cambios)
    public static class MainFrame extends JFrame {

        public MainFrame(Api_Practica_Equi_8 panel3D) {
            super("Cubo/Esfera Sólida 3D con Controles");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new GridLayout(2, 2, 5, 5));
            controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Slider de Intensidad de Luz (Máx 3.0)
            JLabel labelLuz = new JLabel("Intensidad de Luz (Máx 3.0):");
            JSlider sliderLuz = new JSlider(0, 300, 100);
            sliderLuz.setMajorTickSpacing(100);
            sliderLuz.setMinorTickSpacing(25);
            sliderLuz.setPaintTicks(true);
            sliderLuz.setPaintLabels(true);
            sliderLuz.addChangeListener(e -> {
                double intensidad = sliderLuz.getValue() / 100.0;
                panel3D.setIntensidadLuz(intensidad);
            });

            // Slider de Factor de Sombra (0.0 = Máx. Oscuridad)
            JLabel labelSombra = new JLabel("Factor de Sombra (0.0 = Máx. Oscuridad):");
            JSlider sliderSombra = new JSlider(0, 100, (int) (panel3D.factorSombraBase * 100));
            sliderSombra.setMajorTickSpacing(25);
            sliderSombra.setMinorTickSpacing(5);
            sliderSombra.setPaintTicks(true);
            sliderSombra.setPaintLabels(true);
            sliderSombra.addChangeListener(e -> {
                double factor = sliderSombra.getValue() / 100.0;
                panel3D.setFactorSombra(factor);
            });

            controlPanel.add(labelLuz);
            controlPanel.add(sliderLuz);
            controlPanel.add(labelSombra);
            controlPanel.add(sliderSombra);

            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(panel3D, BorderLayout.CENTER);
            getContentPane().add(controlPanel, BorderLayout.SOUTH);

            setSize(800, 850);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Api_Practica_Equi_8 panel = new Api_Practica_Equi_8();
            new MainFrame(panel);
        });
    }
}
