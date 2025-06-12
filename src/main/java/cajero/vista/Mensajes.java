package cajero.vista;

import javax.swing.JOptionPane;

public class Mensajes {

    public static void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    public static String solicitarDato(String mensaje) {
        return JOptionPane.showInputDialog(null, mensaje, "Ingrese dato", JOptionPane.QUESTION_MESSAGE);
    }

    public static boolean confirmar(String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(null, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);
        return opcion == JOptionPane.YES_OPTION;
    }
}
