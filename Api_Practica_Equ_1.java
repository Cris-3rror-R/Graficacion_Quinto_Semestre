import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Api_Practica_1
 * Programa que demuestra transformaciones bidimensionales (rotación, tamaño, posición y orientación)
 * aplicadas a una figura geométrica (rectángulo) usando POO y una interfaz gráfica con sliders.
 */
public class Api_Practica_Equ_1 extends JFrame implements ChangeListener {

    // Paneles principales
    private JPanel PPrincipal, PImage, POpcion, PSalir;

    // Sliders para las transformaciones
    private JSlider sRotacion, sTamano, sPosX, sPosY;

    // Propiedades de la figura
    private double angulo = 0;
    private double escala = 1;
    private double posX = 0;
    private double posY = 0;

    /**
     * Constructor principal que configura toda la interfaz
     */
    public Api_Practica_Equ_1() {
        setTitle("Práctica 1 - Transformaciones 2D con Figura");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel principal con GridLayout
        PPrincipal = new JPanel(new GridLayout(3, 1, 5, 5));

        // Panel de dibujo donde se mostrará la figura
        PImage = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Crear un rectángulo centrado
                double ancho = 150;
                double alto = 100;
                double cx = getWidth() / 2.0;
                double cy = getHeight() / 2.0;

                // Aplicar transformaciones
                AffineTransform transform = new AffineTransform();
                transform.translate(cx + posX, cy + posY);
                transform.rotate(Math.toRadians(angulo));
                transform.scale(escala, escala);
                transform.translate(-ancho / 2, -alto / 2);

                Shape rect = new Rectangle2D.Double(0, 0, ancho, alto);

                // Color de relleno
                g2d.setColor(Color.BLUE);
                g2d.fill(transform.createTransformedShape(rect));

                // Contorno
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(transform.createTransformedShape(rect));
            }
        };
        PImage.setBackground(Color.LIGHT_GRAY);

        // Panel de opciones con sliders
        POpcion = new JPanel(new GridLayout(4, 1, 5, 5));
        POpcion.setBackground(Color.DARK_GRAY);

        // Crear sliders
        sRotacion = crearSlider("Rotación (°)", -180, 180, 0);
        sTamano = crearSlider("Tamaño (%)", 50, 200, 100);
        sPosX = crearSlider("Posición X", -200, 200, 0);
        sPosY = crearSlider("Posición Y", -200, 200, 0);

        // Agregar sliders al panel
        POpcion.add(sRotacion);
        POpcion.add(sTamano);
        POpcion.add(sPosX);
        POpcion.add(sPosY);

        // Panel de salida con botón "Salir"
        PSalir = new JPanel();
        PSalir.setBackground(Color.ORANGE.darker());
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        PSalir.add(btnSalir);

        // Agregar paneles al principal
        PPrincipal.add(PImage);
        PPrincipal.add(POpcion);
        PPrincipal.add(PSalir);

        // Agregar a la ventana
        add(PPrincipal);
    }

    /**
     * Método auxiliar para crear sliders con formato estándar
     */
    private JSlider crearSlider(String titulo, int min, int max, int valorInicial) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, valorInicial);
        slider.setMajorTickSpacing((max - min) / 4);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        slider.setBorder(BorderFactory.createTitledBorder(titulo));
        return slider;
    }

    /**
     * Se ejecuta cuando el usuario mueve algún slider
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == sRotacion)
            angulo = sRotacion.getValue();
        else if (e.getSource() == sTamano)
            escala = sTamano.getValue() / 100.0;
        else if (e.getSource() == sPosX)
            posX = sPosX.getValue();
        else if (e.getSource() == sPosY)
            posY = sPosY.getValue();

        repaint(); // actualiza el dibujo
    }

    /*Método principal*/
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Api_Practica_Equ_1 ventana = new Api_Practica_Equ_1();
            ventana.setVisible(true);
        });
    }
}
