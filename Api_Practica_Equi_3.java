import java.awt.*;
import javax.swing.*;

public class Api_Practica_Equi_3 {

    public static void main(String[] args) {
        JFrame FPrincipal = new JFrame("Practica 3 Fractal");
        FPrincipal.setSize(600, 600);
        FPrincipal.setLocationRelativeTo(null);
        FPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FPrincipal.setLayout(new BorderLayout());

        // Panel superior con ComboBox de funciones
        JPanel PFuncion = new JPanel(new FlowLayout());
        JLabel lblFuncion = new JLabel("Seleccione funcion:");
        String[] ejemplos = {
            "x^2 - 2", "x^3 - 1", "x^4 - 1", "x^5 - x - 1", "x^3 - x",
            "x^2 + 1", "x^3 + x", "x^4 + x^2 + 1", "x^5 - x^3 + x", "x^6 - 1"
        };
        JComboBox<String> comboFunciones = new JComboBox<>(ejemplos);
        JButton btnGenerar = new JButton("Generar Fractal");
        PFuncion.add(lblFuncion);
        PFuncion.add(comboFunciones);
        PFuncion.add(btnGenerar);

        // Panel central para dibujar el fractal
        FractalPanel PLienzo = new FractalPanel();

        // Panel inferior con botones de control
        JPanel POpciones = new JPanel(new FlowLayout());
        JButton btnEliminar = new JButton("Limpiar");
        JButton btnSalir = new JButton("Salir");
        POpciones.add(btnEliminar);
        POpciones.add(btnSalir);

        // Acción del botón "Generar Fractal"
        btnGenerar.addActionListener(e -> {
            String funcion = (String) comboFunciones.getSelectedItem();
            PLienzo.setFuncion(funcion);
            PLienzo.repaint();
        });

        // Acción del botón "Eliminar función"
        btnEliminar.addActionListener(e -> {
            comboFunciones.setSelectedIndex(0);
            PLienzo.clear();
        });

        // Acción del botón "Salir"
        btnSalir.addActionListener(e -> System.exit(0));

        // Agregar paneles a la ventana
        FPrincipal.add(PFuncion, BorderLayout.NORTH);
        FPrincipal.add(PLienzo, BorderLayout.CENTER);
        FPrincipal.add(POpciones, BorderLayout.SOUTH);
        FPrincipal.setVisible(true);
    }
}

// Panel personalizado para dibujar el fractal
class FractalPanel extends JPanel {
    private String funcion = "";

    public void setFuncion(String f) {
        this.funcion = f;
    }

    public void clear() {
        this.funcion = "";
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (funcion.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        double zoom = 4.0;
        int maxIter = 30;

        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                double x0 = (px - width / 2.0) * zoom / width;
                double y0 = (py - height / 2.0) * zoom / height;
                Complex z = new Complex(x0, y0);

                int iter = 0;
                while (iter < maxIter) {
                    Complex fz = evaluarFuncion(z);
                    Complex dfz = derivadaFuncion(z);
                    if (dfz.abs() == 0) break;
                    z = z.subtract(fz.divide(dfz));
                    if (fz.abs() < 1e-6) break;
                    iter++;
                }

                int color = Color.HSBtoRGB((float) iter / maxIter, 1f, iter < maxIter ? 1f : 0);
                g.setColor(new Color(color));
                g.drawLine(px, py, px, py);
            }
        }
    }

    // Evalúa f(z) según la función seleccionada
    private Complex evaluarFuncion(Complex z) {
        switch (funcion) {
            case "x^2 - 2": return z.pow(2).subtract(new Complex(2, 0));
            case "x^3 - 1": return z.pow(3).subtract(new Complex(1, 0));
            case "x^4 - 1": return z.pow(4).subtract(new Complex(1, 0));
            case "x^5 - x - 1": return z.pow(5).subtract(z).subtract(new Complex(1, 0));
            case "x^3 - x": return z.pow(3).subtract(z);
            case "x^2 + 1": return z.pow(2).add(new Complex(1, 0));
            case "x^3 + x": return z.pow(3).add(z);
            case "x^4 + x^2 + 1": return z.pow(4).add(z.pow(2)).add(new Complex(1, 0));
            case "x^5 - x^3 + x": return z.pow(5).subtract(z.pow(3)).add(z);
            case "x^6 - 1": return z.pow(6).subtract(new Complex(1, 0));
            default: return new Complex(0, 0);
        }
    }

    // Evalúa f'(z) según la función seleccionada
    private Complex derivadaFuncion(Complex z) {
        switch (funcion) {
            case "x^2 - 2": return z.multiply(new Complex(2, 0));
            case "x^3 - 1": return z.pow(2).multiply(new Complex(3, 0));
            case "x^4 - 1": return z.pow(3).multiply(new Complex(4, 0));
            case "x^5 - x - 1": return z.pow(4).multiply(new Complex(5, 0)).subtract(new Complex(1, 0));
            case "x^3 - x": return z.pow(2).multiply(new Complex(3, 0)).subtract(new Complex(1, 0));
            case "x^2 + 1": return z.multiply(new Complex(2, 0));
            case "x^3 + x": return z.pow(2).multiply(new Complex(3, 0)).add(new Complex(1, 0));
            case "x^4 + x^2 + 1": return z.pow(3).multiply(new Complex(4, 0)).add(z.multiply(new Complex(2, 0)));
            case "x^5 - x^3 + x": return z.pow(4).multiply(new Complex(5, 0)).subtract(z.pow(2).multiply(new Complex(3, 0))).add(new Complex(1, 0));
            case "x^6 - 1": return z.pow(5).multiply(new Complex(6, 0));
            default: return new Complex(0, 0);
        }
    }
}

// Clase para números complejos
class Complex {
    public double re, im;

    public Complex(double r, double i) {
        re = r;
        im = i;
    }

    public Complex subtract(Complex c) {
        return new Complex(re - c.re, im - c.im);
    }

    public Complex add(Complex c) {
        return new Complex(re + c.re, im + c.im);
    }

    public Complex multiply(Complex c) {
        return new Complex(re * c.re - im * c.im, re * c.im + im * c.re);
    }

    public Complex divide(Complex c) {
        double denom = c.re * c.re + c.im * c.im;
        return new Complex((re * c.re + im * c.im) / denom,
                           (im * c.re - re * c.im) / denom);
    }

    public Complex pow(int n) {
        Complex result = new Complex(re, im);
        for (int i = 1; i < n; i++) {
            result = result.multiply(this);
        }
        return result;
    }

    public double abs() {
        return Math.sqrt(re * re + im * im);
    }
}
