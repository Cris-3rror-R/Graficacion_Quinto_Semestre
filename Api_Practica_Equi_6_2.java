import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Api_Practica_Equi_6_2 extends JPanel implements MouseMotionListener {

    static class Point3D {
        double x, y, z;
        Point3D(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
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
            {0,1},{1,2},{2,3},{3,0},
            {4,5},{5,6},{6,7},{7,4},
            {0,4},{1,5},{2,6},{3,7}
    };

    // Estado de transformaciones (compartidas)
    private double angX = 0, angY = 0, angZ = 0; // rotaciones (rad)
    private double scale = 1.0;                  // 0.33 .. 3.0
    private double tx = 0, ty = 0, tz = 0;       // traslaciones

    // Alternar figura
    private boolean mostrarCubo = true;

    // UI
    private JSlider rotXSlider, rotYSlider, rotZSlider;
    private JSlider scaleSlider, transZSlider;
    private int lastMouseX, lastMouseY;

    public Api_Practica_Equi_6_2() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(900, 700));
        addMouseMotionListener(this);

        // Sliders
        rotXSlider = new JSlider(-180, 180, 0);
        rotYSlider = new JSlider(-180, 180, 0);
        rotZSlider = new JSlider(-180, 180, 0);

        scaleSlider = new JSlider(33, 300, 100);   // 33%..300%, medio=100%
        transZSlider = new JSlider(-300, 300, 0);  // Z

        rotXSlider.setPaintTicks(true); rotXSlider.setMajorTickSpacing(30);
        rotYSlider.setPaintTicks(true); rotYSlider.setMajorTickSpacing(30);
        rotZSlider.setPaintTicks(true); rotZSlider.setMajorTickSpacing(30);
        scaleSlider.setPaintTicks(true); scaleSlider.setMajorTickSpacing(50);
        transZSlider.setPaintTicks(true); transZSlider.setMajorTickSpacing(50);

        ChangeListener onChange = e -> {
            angX = Math.toRadians(rotXSlider.getValue());
            angY = Math.toRadians(rotYSlider.getValue());
            angZ = Math.toRadians(rotZSlider.getValue());
            scale = scaleSlider.getValue() / 100.0;
            tz = transZSlider.getValue();
            repaint();
        };
        rotXSlider.addChangeListener(onChange);
        rotYSlider.addChangeListener(onChange);
        rotZSlider.addChangeListener(onChange);
        scaleSlider.addChangeListener(onChange);
        transZSlider.addChangeListener(onChange);

        // Panel de controles
        JPanel controls = new JPanel(new GridLayout(5, 2));
        controls.add(new JLabel("Rotacion X")); controls.add(rotXSlider);
        controls.add(new JLabel("Rotacion Y")); controls.add(rotYSlider);
        controls.add(new JLabel("Rotacion Z")); controls.add(rotZSlider);
        controls.add(new JLabel("Escala (33% - 300%)")); controls.add(scaleSlider);
        controls.add(new JLabel("Traslacion Z")); controls.add(transZSlider);

        // Frame
        JFrame f = new JFrame("Transformaciones 3D compartidas (Cubo/Esfera)");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());
        f.add(this, BorderLayout.CENTER);
        f.add(controls, BorderLayout.SOUTH);

        // Key Binding para SPACE (funciona aunque los sliders tengan foco)
        InputMap im = f.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = f.getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "toggleShape");
        am.put("toggleShape", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                mostrarCubo = !mostrarCubo;
                repaint();
            }
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
        int sx = (int)(p.x * factor + w / 2.0);
        int sy = (int)(-p.y * factor + h / 2.0);
        return new Point(sx, sy);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        if (mostrarCubo) {
            g2.setColor(Color.WHITE);
            for (int[] e : edges) {
                Point3D p1 = applyTransform(cubeVerts[e[0]]);
                Point3D p2 = applyTransform(cubeVerts[e[1]]);
                Point s1 = project(p1, w, h);
                Point s2 = project(p2, w, h);
                g2.drawLine(s1.x, s1.y, s2.x, s2.y);
            }
            g2.setColor(Color.GREEN);
            g2.drawString("Figura actual: Cubo (SPACE para alternar)", 10, 20);
        } else {
            g2.setColor(Color.CYAN);
            double r = 150.0;
            for (double phi = 0; phi <= Math.PI; phi += Math.PI / 24) {
                for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 24) {
                    double x = r * Math.cos(theta) * Math.sin(phi);
                    double y = r * Math.sin(theta) * Math.sin(phi);
                    double z = r * Math.cos(phi);
                    Point3D tp = applyTransform(new Point3D(x, y, z));
                    Point s = project(tp, w, h);
                    g2.fillOval(s.x, s.y, 2, 2);
                }
            }
            g2.setColor(Color.GREEN);
            g2.drawString("Figura actual: Esfera (SPACE para alternar)", 10, 20);
        }
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
        SwingUtilities.invokeLater(Api_Practica_Equi_6_2::new);
    }
}
