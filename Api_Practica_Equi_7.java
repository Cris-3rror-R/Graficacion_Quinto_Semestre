
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Api_Practica_Equi_7 extends JPanel implements MouseMotionListener {

    static class Point3D {

        double x, y, z;

        Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    // Geometria del cubo
    private final Point3D[] cubeVerts = new Point3D[]{
        new Point3D(-100, -100, -100),
        new Point3D(100, -100, -100),
        new Point3D(100, 100, -100),
        new Point3D(-100, 100, -100),
        new Point3D(-100, -100, 100),
        new Point3D(100, -100, 100),
        new Point3D(100, 100, 100),
        new Point3D(-100, 100, 100)
    };
    private final int[][] edges = new int[][]{
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {4, 5}, {5, 6}, {6, 7}, {7, 4},
        {0, 4}, {1, 5}, {2, 6}, {3, 7}
    };

    // Caras del cubo (para relleno)
    private final int[][] faces = new int[][]{
        {0, 1, 2, 3}, // atras
        {4, 5, 6, 7}, // adelante
        {0, 1, 5, 4}, // abajo
        {3, 2, 6, 7}, // arriba
        {1, 2, 6, 5}, // derecha
        {0, 3, 7, 4} // izquierda
    };

    // Estado de transformaciones (compartidas)
    private double angX = 0, angY = 0, angZ = 0; // rotaciones (rad)
    private double scale = 1.0;                  // 0.33 .. 3.0
    private double tx = 0, ty = 0, tz = 0;       // traslaciones

    // UI
    private JSlider rotXSlider, rotYSlider, rotZSlider;
    private JSlider scaleSlider, transZSlider;
    private JSlider opacitySlider, illuminationSlider, darknessSlider;
    private int lastMouseX, lastMouseY;

    // Parametros de render
    private float opacity = 1.0f;    // 0..1  (0 = invisible, 0.5 = 50%, 1 = full)
    private int illumination = 100;  // 33..300 -> 0.33..3.0, 100 = base (normal)
    private int darkness = 100;      // 33..300 -> invertible factor

    // Color base: azul cobalto (#0047AB)
    private static final Color COBALT = new Color(0x00, 0x47, 0xAB);

    // Modo de relleno: 0=solido, 1=degradado lineal, 2=degradado radial, 3=sombreado
    private int fillMode = 0;

    public Api_Practica_Equi_7() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(900, 700));
        addMouseMotionListener(this);

        // Sliders
        rotXSlider = new JSlider(-180, 180, 0);
        rotYSlider = new JSlider(-180, 180, 0);
        rotZSlider = new JSlider(-180, 180, 0);

        scaleSlider = new JSlider(33, 300, 100);   // 33%..300%, medio=100%
        transZSlider = new JSlider(-300, 300, 0);  // Z

        // Opacidad: 0% invisible .. 100% completo. Valor intermedio 50% => 0.5
        opacitySlider = new JSlider(0, 100, 100);

        // Iluminacion: 33 .. 300 (33 = mas oscuro, 100 = normal, 300 = muy claro)
        illuminationSlider = new JSlider(33, 300, 100);

        // Oscuridad: 33 .. 300 (se usa como divisor/inverso)
        darknessSlider = new JSlider(33, 300, 100);

        rotXSlider.setPaintTicks(true);
        rotXSlider.setMajorTickSpacing(30);
        rotYSlider.setPaintTicks(true);
        rotYSlider.setMajorTickSpacing(30);
        rotZSlider.setPaintTicks(true);
        rotZSlider.setMajorTickSpacing(30);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setMajorTickSpacing(50);
        transZSlider.setPaintTicks(true);
        transZSlider.setMajorTickSpacing(50);
        opacitySlider.setPaintTicks(true);
        opacitySlider.setMajorTickSpacing(25);
        illuminationSlider.setPaintTicks(true);
        illuminationSlider.setMajorTickSpacing(50);
        darknessSlider.setPaintTicks(true);
        darknessSlider.setMajorTickSpacing(50);

        ChangeListener onChange = e -> {
            angX = Math.toRadians(rotXSlider.getValue());
            angY = Math.toRadians(rotYSlider.getValue());
            angZ = Math.toRadians(rotZSlider.getValue());
            scale = scaleSlider.getValue() / 100.0;
            tz = transZSlider.getValue();
            opacity = opacitySlider.getValue() / 100.0f;
            illumination = illuminationSlider.getValue();
            darkness = darknessSlider.getValue();
            repaint();
        };
        rotXSlider.addChangeListener(onChange);
        rotYSlider.addChangeListener(onChange);
        rotZSlider.addChangeListener(onChange);
        scaleSlider.addChangeListener(onChange);
        transZSlider.addChangeListener(onChange);
        opacitySlider.addChangeListener(onChange);
        illuminationSlider.addChangeListener(onChange);
        darknessSlider.addChangeListener(onChange);

        // Panel de controles
        JPanel controls = new JPanel(new GridLayout(8, 2));
        controls.add(new JLabel("Rotacion X"));
        controls.add(rotXSlider);
        controls.add(new JLabel("Rotacion Y"));
        controls.add(rotYSlider);
        controls.add(new JLabel("Rotacion Z"));
        controls.add(rotZSlider);
        controls.add(new JLabel("Escala (33% - 300%)"));
        controls.add(scaleSlider);
        controls.add(new JLabel("Traslacion Z"));
        controls.add(transZSlider);

        controls.add(new JLabel("Opacidad (0% invisible - 100% completo)"));
        controls.add(opacitySlider);
        controls.add(new JLabel("Iluminacion (33 - 300) 100=normal, 300=mas claro, 33=mas oscuro"));
        controls.add(illuminationSlider);
        controls.add(new JLabel("Oscuridad (33 - 300) - usada como divisor (inversa)"));
        controls.add(darknessSlider);

        // Frame
        JFrame f = new JFrame("Transformaciones 3D (Cubo) - Opacidad/Ilum/Oscuridad");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());
        f.add(this, BorderLayout.CENTER);
        f.add(controls, BorderLayout.SOUTH);

        // Crear barra de menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fillMenu = new JMenu("Relleno");

        JMenuItem solidItem = new JMenuItem("Relleno Solido");
        solidItem.addActionListener(e -> {
            fillMode = 0;
            repaint();
        });
        fillMenu.add(solidItem);

        JMenuItem linearGradItem = new JMenuItem("Degradado Lineal");
        linearGradItem.addActionListener(e -> {
            fillMode = 1;
            repaint();
        });
        fillMenu.add(linearGradItem);

        JMenuItem radialGradItem = new JMenuItem("Degradado Radial");
        radialGradItem.addActionListener(e -> {
            fillMode = 2;
            repaint();
        });
        fillMenu.add(radialGradItem);

        JMenuItem shadingItem = new JMenuItem("Sombreado (Shading)");
        shadingItem.addActionListener(e -> {
            fillMode = 3;
            repaint();
        });
        fillMenu.add(shadingItem);

        menuBar.add(fillMenu);
        f.setJMenuBar(menuBar);

        // Key Binding para SPACE: NO altera la figura (no-op)
        InputMap im = f.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = f.getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "noOp");
        am.put("noOp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* no hace nada */ }
        });

        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // Pipeline de transformacion
    private Point3D applyTransform(Point3D p) {
        double cx = Math.cos(angX), sx = Math.sin(angX);
        double cy = Math.cos(angY), sy = Math.sin(angY);
        double cz = Math.cos(angZ), sz = Math.sin(angZ);

        // Rot X
        double y = p.y * cx - p.z * sx;
        double z = p.y * sx + p.z * cx;
        double x = p.x;

        // Rot Y
        double x2 = x * cy + z * sy;
        double z2 = -x * sy + z * cy;

        // Rot Z
        double x3 = x2 * cz - y * sz;
        double y3 = x2 * sz + y * cz;
        double z3 = z2;

        // Escala + traslacion
        return new Point3D(x3 * scale + tx, y3 * scale + ty, z3 * scale + tz);
    }

    private Point project(Point3D p, int w, int h) {
        double d = 400.0;
        double factor = d / (d + p.z);
        int sx = (int) (p.x * factor + w / 2.0);
        int sy = (int) (-p.y * factor + h / 2.0);
        return new Point(sx, sy);
    }

    // Normaliza vector
    private double[] normalize(double[] v) {
        double l = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if (l == 0) {
            return new double[]{0, 0, 0};
        }
        return new double[]{v[0] / l, v[1] / l, v[2] / l};
    }

    // Producto punto
    private double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    // Resta de vectores
    private double[] sub(Point3D a, Point3D b) {
        return new double[]{a.x - b.x, a.y - b.y, a.z - b.z};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Direccion de luz
        double azL = Math.toRadians(45), elL = Math.toRadians(60);
        double[] L = normalize(new double[]{Math.cos(azL) * Math.cos(elL), Math.sin(azL) * Math.cos(elL), Math.sin(elL)});

        // Transformar vertices
        Point3D[] tv = new Point3D[cubeVerts.length];
        Point[] pv = new Point[cubeVerts.length];
        for (int i = 0; i < cubeVerts.length; i++) {
            tv[i] = applyTransform(cubeVerts[i]);
            pv[i] = project(tv[i], w, h);
        }

        // si opacity == 0 no pintar
        if (opacity <= 0f) {
            g2.setColor(Color.GREEN);
            g2.drawString("Figura actual: Cubo (invisible)", 10, 20);
            return;
        }

        // calcular factor de iluminacion centralizado
        double illumFactor = illumination / 100.0;
        double darkFactor = darkness / 100.0;
        double globalFactor = illumFactor / darkFactor;
        if (globalFactor < 0) {
            globalFactor = 0;
        }

        // Si opacidad es baja dibujamos wireframe con alpha
        if (opacity < 0.5f) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2.setColor(COBALT);
            for (int[] e : edges) {
                Point s1 = pv[e[0]], s2 = pv[e[1]];
                g2.drawLine(s1.x, s1.y, s2.x, s2.y);
            }
            for (Point p : pv) {
                g2.fillOval(p.x - 3, p.y - 3, 6, 6);
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        } else {
            // Relleno segun fillMode
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

            for (int[] face : faces) {
                int[] xs = new int[face.length], ys = new int[face.length];
                Point[] facePoints = new Point[face.length];
                Point3D[] faceVerts3D = new Point3D[face.length];
                for (int i = 0; i < face.length; i++) {
                    facePoints[i] = pv[face[i]];
                    faceVerts3D[i] = tv[face[i]];
                    xs[i] = facePoints[i].x;
                    ys[i] = facePoints[i].y;
                }

                if (fillMode == 0) {
                    // Relleno solido uniforme
                    int rr = (int) Math.min(255, Math.max(0, COBALT.getRed() * globalFactor));
                    int gg = (int) Math.min(255, Math.max(0, COBALT.getGreen() * globalFactor));
                    int bb = (int) Math.min(255, Math.max(0, COBALT.getBlue() * globalFactor));
                    g2.setColor(new Color(rr, gg, bb));
                    g2.fillPolygon(xs, ys, face.length);

                } else if (fillMode == 1) {
                    // Degradado lineal
                    Point p1 = facePoints[0];
                    Point p2 = facePoints[face.length / 2];
                    Color c1 = new Color(
                            (int) Math.min(255, Math.max(0, COBALT.getRed() * globalFactor * 0.5)),
                            (int) Math.min(255, Math.max(0, COBALT.getGreen() * globalFactor * 0.5)),
                            (int) Math.min(255, Math.max(0, COBALT.getBlue() * globalFactor * 0.5))
                    );
                    Color c2 = new Color(
                            (int) Math.min(255, Math.max(0, COBALT.getRed() * globalFactor)),
                            (int) Math.min(255, Math.max(0, COBALT.getGreen() * globalFactor)),
                            (int) Math.min(255, Math.max(0, COBALT.getBlue() * globalFactor))
                    );
                    GradientPaint grad = new GradientPaint(p1.x, p1.y, c1, p2.x, p2.y, c2);
                    g2.setPaint(grad);
                    g2.fillPolygon(xs, ys, face.length);

                } else if (fillMode == 2) {
                    // Degradado radial (simulado con GradientPaint)
                    Point center = new Point(
                            (xs[0] + xs[face.length - 1]) / 2,
                            (ys[0] + ys[face.length - 1]) / 2
                    );
                    Color cCenter = new Color(
                            (int) Math.min(255, Math.max(0, COBALT.getRed() * globalFactor)),
                            (int) Math.min(255, Math.max(0, COBALT.getGreen() * globalFactor)),
                            (int) Math.min(255, Math.max(0, COBALT.getBlue() * globalFactor))
                    );
                    Color cEdge = new Color(
                            (int) Math.min(255, Math.max(0, COBALT.getRed() * globalFactor * 0.3)),
                            (int) Math.min(255, Math.max(0, COBALT.getGreen() * globalFactor * 0.3)),
                            (int) Math.min(255, Math.max(0, COBALT.getBlue() * globalFactor * 0.3))
                    );
                    GradientPaint grad = new GradientPaint(center.x, center.y, cCenter,
                            xs[0], ys[0], cEdge);
                    g2.setPaint(grad);
                    g2.fillPolygon(xs, ys, face.length);

                } else if (fillMode == 3) {
                    // Sombreado (Shading) - calculo de normal y iluminacion por cara
                    Point3D p0 = faceVerts3D[0], p1 = faceVerts3D[1], p2 = faceVerts3D[2];
                    double[] u = sub(p1, p0), v = sub(p2, p0);
                    double[] n = new double[]{u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0]};
                    n = normalize(n);
                    double dotL = Math.max(0, dot(n, L));
                    double bright = 0.2 + dotL * globalFactor;
                    int rr = (int) Math.min(255, Math.max(0, COBALT.getRed() * bright));
                    int gg = (int) Math.min(255, Math.max(0, COBALT.getGreen() * bright));
                    int bb = (int) Math.min(255, Math.max(0, COBALT.getBlue() * bright));
                    g2.setColor(new Color(rr, gg, bb));
                    g2.fillPolygon(xs, ys, face.length);
                }
            }

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        g2.setColor(Color.GREEN);
        String fillModeStr = "";
        switch (fillMode) {
            case 0:
                fillModeStr = "Relleno Solido";
                break;
            case 1:
                fillModeStr = "Degradado Lineal";
                break;
            case 2:
                fillModeStr = "Degradado Radial";
                break;
            case 3:
                fillModeStr = "Sombreado (Shading)";
                break;
        }
        g2.drawString(String.format("Figura: Cubo   Opacidad: %d%%   Ilum: %d   Osc: %d   Modo: %s",
                Math.round(opacity * 100), illumination, darkness, fillModeStr), 10, 20);
    }

    // Traslacion XY con mouse
    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - lastMouseX;
        int dy = e.getY() - lastMouseY;
        tx += dx;
        ty += dy;
        lastMouseX = e.getX();
        lastMouseY = e.getY();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Api_Practica_Equi_7::new);
    }
}
