import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

/**
 * Api_Practica_Equipo_1
 * Programa que demuestra transformaciones bidimensionales (rotación, tamaño, posición y orientación)
 */
public class Api_Practica_Ind_1 extends JFrame implements ChangeListener {

    // Paneles principales
    private JPanel PPrincipal, PImage, POpcion, PSalir;

    // Sliders para las transformaciones
    private JSlider sRotacion, sTamano, sPosX, sPosY, sAncho, sAlto;

    // Propiedades de la figura
    private double angulo = 0;
    private double escala = 1;
    private double posX = 0;
    private double posY = 0;
    private double anchoRect = 150;
    private double altoRect = 100;

    /**
     * Constructor principal que configura toda la interfaz
     */
    public Api_Practica_Ind_1() {
        setTitle("Practica 1 - Transformaciones Graficacion 2D");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(204, 119, 34)); // fondo ocre

        // Panel principal con GridLayout
        PPrincipal = new JPanel(new GridLayout(3, 1, 5, 5));
        PPrincipal.setOpaque(false);

        // Panel de dibujo donde se mostrará la figura
        PImage = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                double cx = getWidth() / 2.0;
                double cy = getHeight() / 2.0;

                // Crear romboide
                Path2D romboide = new Path2D.Double();
                romboide.moveTo(0, 0);
                romboide.lineTo(anchoRect, 0);
                romboide.lineTo(anchoRect - 30, altoRect);
                romboide.lineTo(-30, altoRect);
                romboide.closePath();

                // Aplicar transformaciones
                AffineTransform transform = new AffineTransform();
                transform.translate(cx + posX, cy + posY);
                transform.rotate(Math.toRadians(angulo));
                transform.scale(escala, escala);
                transform.translate(-anchoRect / 2, -altoRect / 2);

                Shape figura = transform.createTransformedShape(romboide);

                // Color escarlata
                g2d.setColor(new Color(255, 36, 0));
                g2d.fill(figura);

                // Contorno
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(figura);
            }
        };
        PImage.setBackground(Color.WHITE);

        // Panel de opciones con sliders
        POpcion = new JPanel(new GridLayout(6, 1, 10, 10));
        POpcion.setBackground(Color.LIGHT_GRAY);

        // Crear sliders
        sRotacion = crearSlider("Rotacion (°)", -180, 180, 0);
        sTamano = crearSlider("Tamano (%)", 50, 200, 100);
        sPosX = crearSlider("Posicion X", -200, 200, 0);
        sPosY = crearSlider("Posicion Y", -200, 200, 0);
        sAncho = crearSlider("Ancho (px)", 5, 300, 150);
        sAlto = crearSlider("Alto (px)", 5, 300, 100);

        // Agregar sliders al panel
        POpcion.add(sRotacion);
        POpcion.add(sTamano);
        POpcion.add(sPosX);
        POpcion.add(sPosY);
        POpcion.add(sAncho);
        POpcion.add(sAlto);

        // Panel de salida con botón "Salir"
        PSalir = new JPanel();
        PSalir.setBackground(Color.ORANGE.darker());
        JButton btnSalir = new JButton("Salir");
        btnSalir.setFont(new Font("Arial", Font.BOLD, 18));
        btnSalir.setPreferredSize(new Dimension(150, 50));
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
     * Método auxiliar para crear sliders con formato estándar y mayor tamaño
     */
    private JSlider crearSlider(String titulo, int min, int max, int valorInicial) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, valorInicial);
        slider.setMajorTickSpacing((max - min) / 4);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);

        // Aumentar altura visual y espacio
        slider.setPreferredSize(new Dimension(800, 70));
        slider.setFont(new Font("Arial", Font.BOLD, 14));
        slider.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), titulo,
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.BLACK)
        ));

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
        else if (e.getSource() == sAncho)
            anchoRect = sAncho.getValue();
        else if (e.getSource() == sAlto)
            altoRect = sAlto.getValue();

        repaint(); // actualiza el dibujo
    }

    /**
     * Método principal
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Api_Practica_Ind_1 ventana = new Api_Practica_Ind_1();
            ventana.setVisible(true);
        });
    }
}
