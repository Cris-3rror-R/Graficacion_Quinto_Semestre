import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

public class Api_Practica_Equi_4 extends JFrame {

    private JTextArea areaTexto;
    private JComboBox<String> comboFuentes;
    private JPanel panelVista;
    private JSlider sliderTamanio;
    private JCheckBox chkNegrita, chkCursiva, chkSubrayado;
    private JButton btnNormal, btnLimpiar, btnSalir;

    public Api_Practica_Equi_4() {
        setTitle("Practica 4 Fuentes de Letra");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior con controles
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] fuentes = {"Arial", "Comic Sans MS", "Tahoma", "ZOV"};
        comboFuentes = new JComboBox<>(fuentes);
        panelSuperior.add(new JLabel("Fuente:"));
        panelSuperior.add(comboFuentes);

        sliderTamanio = new JSlider(10, 64, 20);
        sliderTamanio.setMajorTickSpacing(10);
        sliderTamanio.setPaintTicks(true);
        sliderTamanio.setPaintLabels(true);
        panelSuperior.add(new JLabel("Tamaño:"));
        panelSuperior.add(sliderTamanio);

        chkNegrita = new JCheckBox("Negrita");
        chkCursiva = new JCheckBox("Cursiva");
        chkSubrayado = new JCheckBox("Subrayado");
        btnNormal = new JButton("Texto Normal");
        btnLimpiar = new JButton("Limpiar");
        btnSalir = new JButton("Salir");

        panelSuperior.add(chkNegrita);
        panelSuperior.add(chkCursiva);
        panelSuperior.add(chkSubrayado);
        panelSuperior.add(btnNormal);
        panelSuperior.add(btnLimpiar);
        panelSuperior.add(btnSalir);

        add(panelSuperior, BorderLayout.NORTH);

        // Area de texto
        areaTexto = new JTextArea("Escribe aquí tu texto...");
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll, BorderLayout.CENTER);

        // Panel de vista previa
        panelVista = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                String texto = areaTexto.getText();
                String fuente = (String) comboFuentes.getSelectedItem();
                int estilo = (chkNegrita.isSelected() ? Font.BOLD : 0) | (chkCursiva.isSelected() ? Font.ITALIC : 0);
                int tamanio = sliderTamanio.getValue();

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.BLACK);

                if (fuente.equals("ZOV")) {
                    dibujarZOV(g2, texto, 20, 60, tamanio);
                } else {
                    Font font = new Font(fuente, estilo, tamanio);
                    g2.setFont(font);
                    FontMetrics fm = g2.getFontMetrics();
                    int y = 60;
                    for (String linea : texto.split("\n")) {
                        g2.drawString(linea, 20, y);
                        if (chkSubrayado.isSelected()) {
                            int width = fm.stringWidth(linea);
                            g2.drawLine(20, y + 2, 20 + width, y + 2);
                        }
                        y += fm.getHeight();
                    }
                }
            }
        };
        panelVista.setPreferredSize(new Dimension(800, 200));
        panelVista.setBackground(Color.WHITE);
        panelVista.setBorder(BorderFactory.createTitledBorder("Vista previa"));
        add(panelVista, BorderLayout.SOUTH);

        // Eventos
        ActionListener actualizarVista = e -> panelVista.repaint();
        comboFuentes.addActionListener(actualizarVista);
        sliderTamanio.addChangeListener(e -> panelVista.repaint());
        chkNegrita.addActionListener(actualizarVista);
        chkCursiva.addActionListener(actualizarVista);
        chkSubrayado.addActionListener(actualizarVista);

        btnNormal.addActionListener(e -> {
            chkNegrita.setSelected(false);
            chkCursiva.setSelected(false);
            chkSubrayado.setSelected(false);
            panelVista.repaint();
        });

        btnLimpiar.addActionListener(e -> {
            areaTexto.setText("");
            panelVista.repaint();
        });

        btnSalir.addActionListener(e -> System.exit(0));

        areaTexto.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { panelVista.repaint(); }
            public void removeUpdate(DocumentEvent e) { panelVista.repaint(); }
            public void insertUpdate(DocumentEvent e) { panelVista.repaint(); }
        });

        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void dibujarZOV(Graphics2D g2, String texto, int x, int y, int tamanio) {
        texto = texto.toUpperCase();
        double escala = tamanio / 36.0;
        g2.scale(escala, escala);
        for (char c : texto.toCharArray()) {
            Shape letra = crearLetraZOV(c, x, y);
            if (letra != null) {
                g2.fill(letra);
            }
            x += 40;
            if (x > getWidth() / escala - 100) {
                x = 20;
                y += 60;
            }
        }
        g2.scale(1 / escala, 1 / escala);
    }

    private Shape crearLetraZOV(char c, int x, int y) {
        Path2D path = new Path2D.Double();
        switch (c) {
            case 'A':
                path.moveTo(x + 10, y);
                path.lineTo(x + 25, y - 50);
                path.lineTo(x + 40, y);
                path.moveTo(x + 15, y - 25);
                path.lineTo(x + 35, y - 25);
                break;
            case 'B':
                path.moveTo(x, y);
                path.lineTo(x, y - 50);
                path.quadTo(x + 30, y - 45, x + 25, y - 25);
                path.quadTo(x + 30, y - 5, x, y);
                break;
            case 'C':
                path.moveTo(x + 35, y - 50);
                path.lineTo(x + 10, y - 50);
                path.lineTo(x, y - 25);
                path.lineTo(x + 10, y);
                path.lineTo(x + 35, y);
                break;
            case 'E':
                path.moveTo(x + 30, y - 50);
                path.lineTo(x, y - 50);
                path.lineTo(x, y);
                path.lineTo(x + 30, y);
                path.moveTo(x, y - 25);
                path.lineTo(x + 20, y - 25);
                break;
            case 'G':
                path.moveTo(x + 35, y - 50);
                path.lineTo(x + 10, y - 50);
                path.lineTo(x, y - 25);
                path.lineTo(x + 10, y);
                path.lineTo(x + 35, y);
                path.moveTo(x + 35, y);
                path.lineTo(x + 35, y - 25);
                path.lineTo(x + 20, y - 25);
                break;
            case 'K':
                path.moveTo(x, y);
                path.lineTo(x, y - 50);
                path.moveTo(x, y - 25);
                path.lineTo(x + 25, y - 50);
                path.moveTo(x, y - 25);
                path.lineTo(x + 25, y);
                break;
            case 'L':
                path.moveTo(x, y - 50);
                path.lineTo(x, y);
                path.lineTo(x + 30, y);
                break;
            case 'M':
                path.moveTo(x, y);
                path.lineTo(x, y - 50);
                path.lineTo(x + 15, y - 25);
                path.lineTo(x + 30, y - 50);
                path.lineTo(x + 30, y);
                break;
            case 'N':
                path.moveTo(x, y);
                path.lineTo(x, y - 50);
                path.lineTo(x + 30, y);
                path.lineTo(x + 30, y - 50);
                break;
            case 'O':
                path.moveTo(x + 15, y - 50);
                path.quadTo(x + 40, y - 25, x + 15, y);
                path.quadTo(x - 10, y - 25, x + 15, y - 50);
                break;
            case 'R':
                path.moveTo(x, y);
                path.lineTo(x, y - 50);
                path.quadTo(x + 30, y - 45, x + 20, y - 25);
                path.lineTo(x + 30, y);
                break;
            case ' ':
                return null;
            default:
                path.moveTo(x + 10, y - 40);
                path.lineTo(x + 30, y - 40);
                path.lineTo(x + 30, y - 10);
                path.lineTo(x + 10, y - 10);
                path.closePath();
        }
        return path;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Api_Practica_Equi_4::new);
    }
}
