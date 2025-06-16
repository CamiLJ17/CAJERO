package cajero.vista;

import javax.swing.*;
import java.awt.*;

public class Mensajes {

    private static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FUENTE_NEGRITA = new Font("Segoe UI", Font.BOLD, 14);

    private static JLabel crearEtiqueta(String mensaje, boolean negrita) {
        JLabel etiqueta = new JLabel("<html>" + mensaje + "</html>");
        etiqueta.setFont(negrita ? FUENTE_NEGRITA : FUENTE_NORMAL);
        etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
        return etiqueta;
    }

    public static void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
            null,
            crearEtiqueta(mensaje, false),
            "Información",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
            null,
            crearEtiqueta(mensaje, true),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(
            null,
            crearEtiqueta(mensaje, true),
            "Advertencia",
            JOptionPane.WARNING_MESSAGE
        );
    }

    public static String solicitarDato(String mensaje) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel etiqueta = new JLabel(mensaje);
        etiqueta.setFont(FUENTE_NORMAL);
        JTextField campo = new JTextField();
        campo.setFont(FUENTE_NORMAL);

        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(8));
        panel.add(campo);

        int opcion = JOptionPane.showConfirmDialog(
            null,
            panel,
            "Ingrese los datos",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        return (opcion == JOptionPane.OK_OPTION) ? campo.getText().trim() : null;
    }

    public static boolean confirmar(String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(
            null,
            crearEtiqueta(mensaje, false),
            "Confirmar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return opcion == JOptionPane.YES_OPTION;
    }
}
