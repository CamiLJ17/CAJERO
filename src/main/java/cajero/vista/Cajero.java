package cajero.vista;

import cajero.controlador.CajeroControlador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Cajero {

    private static CajeroControlador controlador = new CajeroControlador();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Cajero::crearInterfaz);
    }

    public static void crearInterfaz() {
        JFrame frame = new JFrame("Cajero Automático");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel(new GridLayout(4, 1));

        JTextField campoCuenta = new JTextField();
        JPasswordField campoPIN = new JPasswordField();

        JButton btnIngresar = new JButton("Iniciar Sesión");
        JLabel mensaje = new JLabel("", SwingConstants.CENTER);

        panel.add(new JLabel("Número de Cuenta:"));
        panel.add(campoCuenta);
        panel.add(new JLabel("PIN:"));
        panel.add(campoPIN);
        panel.add(btnIngresar);
        panel.add(mensaje);

        btnIngresar.addActionListener((ActionEvent e) -> {
            String cuenta = campoCuenta.getText();
            String pin = new String(campoPIN.getPassword());

            boolean acceso = controlador.iniciarSesion(cuenta, pin);
            if (acceso) {
                mensaje.setText("✅ ¡Acceso correcto!");
                mostrarMenuPrincipal(cuenta);
                frame.dispose();
            } else {
                mensaje.setText("❌ Error de autenticación.");
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void mostrarMenuPrincipal(String cuentaId) {
        JFrame menuFrame = new JFrame("Operaciones");
        menuFrame.setSize(300, 250);
        JPanel panel = new JPanel(new GridLayout(5, 1));

        JButton btnSaldo = new JButton("Consultar saldo");
        JButton btnRetiro = new JButton("Retirar dinero");
        JButton btnDeposito = new JButton("Depositar dinero");
        JButton btnSalir = new JButton("Salir");
        JLabel resultado = new JLabel("", SwingConstants.CENTER);

        panel.add(btnSaldo);
        panel.add(btnRetiro);
        panel.add(btnDeposito);
        panel.add(btnSalir);
        panel.add(resultado);

        btnSaldo.addActionListener(e -> {
            double saldo = controlador.consultarSaldo(cuentaId);
            resultado.setText("Saldo: $" + saldo);
        });

        btnRetiro.addActionListener(e -> {
            String montoStr = JOptionPane.showInputDialog("Monto a retirar:");
            if (montoStr != null) {
                double monto = Double.parseDouble(montoStr);
                boolean exito = controlador.retirarDinero(cuentaId, monto);
                double saldo = controlador.consultarSaldo(cuentaId);
                resultado.setText(exito ? "Retiro exitoso." + "Su nuevo saldo es de: $" + saldo: "Retiro fallido" + "Su nuevo saldo es de: $" + saldo);
            }
        });

        btnDeposito.addActionListener(e -> {
            String montoStr = JOptionPane.showInputDialog("Monto a depositar:");
            if (montoStr != null) {
                double monto = Double.parseDouble(montoStr);
                controlador.ingresarDinero(cuentaId, monto);
                double saldo = controlador.consultarSaldo(cuentaId);
                resultado.setText("Depósito realizado"+ "Su nuevo saldo es de: $" + saldo);
            }
        });

        btnSalir.addActionListener(e -> {
            menuFrame.dispose();
            crearInterfaz(); // Regresar al login
        });

        menuFrame.add(panel);
        menuFrame.setVisible(true);
    }
}
