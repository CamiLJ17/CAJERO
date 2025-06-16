package cajero.vista;

import cajero.controlador.CajeroControlador;
import cajero.modelo.Transaccion;
import cajero.modelo.HistorialTransacciones;

import javax.swing.*;
import java.awt.*;
import java.util.List;
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
        JButton btnCrearCliente = new JButton("Crear Cliente");
        JButton btnRecuperarPIN = new JButton("¿Olvidaste tu PIN?");
        JLabel mensaje = new JLabel("", SwingConstants.CENTER);

        panel.add(new JLabel("Número de Cuenta:"));
        panel.add(campoCuenta);
        panel.add(new JLabel("PIN:"));
        panel.add(campoPIN);
        panel.add(btnIngresar);
        panel.add(btnCrearCliente);  
        panel.add(btnRecuperarPIN);
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

        btnCrearCliente.addActionListener(e -> {
            JPanel panelNewClient = new JPanel(new GridLayout(0, 1));
            JTextField campoNombre = new JTextField();
            JTextField campoPin = new JTextField();
            JTextField campoSaldo = new JTextField();

            // Preguntas de seguridad
            String[] preguntas = {
                "¿Cuál es el nombre de tu mascota?",
                "¿Cuál es tu color favorito?",
                "¿En qué ciudad naciste?",
                "¿Cuál es tu comida favorita?",
                "¿Nombre de tu mejor amigo de infancia?"
            };

            JComboBox<String> comboPreguntas = new JComboBox<>(preguntas);
            JTextField campoRespuesta = new JTextField();

            // Agregar campos al panel
            panelNewClient.add(new JLabel("Nombre del cliente:"));
            panelNewClient.add(campoNombre);
            panelNewClient.add(new JLabel("PIN (4 dígitos):"));
            panelNewClient.add(campoPin);
            panelNewClient.add(new JLabel("Saldo inicial:"));
            panelNewClient.add(campoSaldo);
            panelNewClient.add(new JLabel("Pregunta de seguridad:"));
            panelNewClient.add(comboPreguntas);
            panelNewClient.add(new JLabel("Respuesta:"));
            panelNewClient.add(campoRespuesta);

            int resultado = JOptionPane.showConfirmDialog(null, panelNewClient, "Crear nuevo cliente",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (resultado == JOptionPane.OK_OPTION) {
                String nombre = campoNombre.getText();
                String pin = campoPin.getText();
                double saldo;

                try {
                    saldo = Double.parseDouble(campoSaldo.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "❌ Saldo inválido. Debe ser un número.");
                    return;
                }

                if (nombre.trim().isEmpty() || !pin.matches("\\d{4}")) {
                    JOptionPane.showMessageDialog(null, "❌ Datos inválidos. Asegúrate de ingresar un nombre y un PIN de 4 dígitos.");
                    return;
                }

                String preguntaSeleccionada = (String) comboPreguntas.getSelectedItem();
                String respuesta = campoRespuesta.getText().trim();

                if (respuesta.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "❌ Debes ingresar una respuesta a la pregunta de seguridad.");
                    return;
                }

                // Llamar al controlador con la nueva info
                String nuevaCuentaId = controlador.registrarNuevoCliente(nombre, pin, saldo, preguntaSeleccionada, respuesta);
                JOptionPane.showMessageDialog(null, "✅ Cliente creado exitosamente.\nNúmero de cuenta: " + nuevaCuentaId);
            }
        });
        
        btnRecuperarPIN.addActionListener(e -> {
            // Primer panel: pedir número de cuenta
            JPanel panelRecuperacion = new JPanel(new GridLayout(0, 1));
            JTextField campoCuentaRecuperacion = new JTextField();

            panelRecuperacion.add(new JLabel("Número de cuenta:"));
            panelRecuperacion.add(campoCuentaRecuperacion);

            int resultado = JOptionPane.showConfirmDialog(null, panelRecuperacion, "Recuperar PIN", JOptionPane.OK_CANCEL_OPTION);

            if (resultado == JOptionPane.OK_OPTION) {
                String cuentaId = campoCuentaRecuperacion.getText().trim();
                String pregunta = controlador.obtenerPreguntaSeguridad(cuentaId);

                if (pregunta == null) {
                    JOptionPane.showMessageDialog(null, "❌ Cuenta no encontrada.");
                    return;
                }

                // Segundo panel: validar respuesta a la pregunta
                JPanel panelSeguridad = new JPanel(new GridLayout(0, 1));
                panelSeguridad.add(new JLabel("Pregunta de seguridad:"));
                panelSeguridad.add(new JLabel(pregunta));

                JTextField campoRespuesta = new JTextField();
                panelSeguridad.add(new JLabel("Tu respuesta:"));
                panelSeguridad.add(campoRespuesta);

                int confirmar = JOptionPane.showConfirmDialog(null, panelSeguridad, "Verificación de identidad", JOptionPane.OK_CANCEL_OPTION);

                if (confirmar == JOptionPane.OK_OPTION) {
                    String respuesta = campoRespuesta.getText().trim();

                    if (!controlador.validarRespuestaSeguridad(cuentaId, respuesta)) {
                        JOptionPane.showMessageDialog(null, "❌ Respuesta incorrecta.");
                        return;
                    }

                    // Tercer panel: ingresar nuevo PIN dos veces
                    boolean pinValido = false;

                    while (!pinValido) {
                        JPanel panelNuevoPin = new JPanel(new GridLayout(0, 1));
                        JPasswordField campoNuevoPin1 = new JPasswordField();
                        JPasswordField campoNuevoPin2 = new JPasswordField();

                        panelNuevoPin.add(new JLabel("Nuevo PIN (4 dígitos):"));
                        panelNuevoPin.add(campoNuevoPin1);
                        panelNuevoPin.add(new JLabel("Confirmar nuevo PIN:"));
                        panelNuevoPin.add(campoNuevoPin2);

                        int confirmacionFinal = JOptionPane.showConfirmDialog(null, panelNuevoPin, "Establecer nuevo PIN", JOptionPane.OK_CANCEL_OPTION);

                        if (confirmacionFinal != JOptionPane.OK_OPTION) {
                            break; // El usuario canceló
                        }

                        String nuevoPin1 = new String(campoNuevoPin1.getPassword());
                        String nuevoPin2 = new String(campoNuevoPin2.getPassword());

                        if (!nuevoPin1.matches("\\d{4}")) {
                            JOptionPane.showMessageDialog(null, "❌ El PIN debe tener exactamente 4 dígitos.");
                        } else if (!nuevoPin1.equals(nuevoPin2)) {
                            JOptionPane.showMessageDialog(null, "❌ Los PIN ingresados no coinciden.");
                        } else {
                            boolean exito = controlador.recuperarContrasena(cuentaId, respuesta, nuevoPin1, nuevoPin2);
                            pinValido = true; // Solo salimos si se valida correctamente
                        }
                    }

                }
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
        JButton btnTransferencia = new JButton("Transferir dinero");
        JButton btnHistorial = new JButton("Ver historial");
        JButton btnConfiguracion = new JButton("Configuración de cuenta");

        JButton btnSalir = new JButton("Salir");
        JLabel resultado = new JLabel("", SwingConstants.CENTER);

        panel.add(btnSaldo);
        panel.add(btnRetiro);
        panel.add(btnDeposito);
        panel.add(btnTransferencia);
        panel.add(btnHistorial);
        panel.add(btnConfiguracion);

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

        btnTransferencia.addActionListener(e -> {
            JPanel panelTransferencia = new JPanel(new GridLayout(2, 2));
            JTextField campoCuentaDestino = new JTextField();
            JTextField campoMonto = new JTextField();

            panelTransferencia.add(new JLabel("Cuenta destino:"));
            panelTransferencia.add(campoCuentaDestino);
            panelTransferencia.add(new JLabel("Monto a transferir:"));
            panelTransferencia.add(campoMonto);

            int opcion = JOptionPane.showConfirmDialog(
                null,
                panelTransferencia,
                "Transferencia entre cuentas",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (opcion == JOptionPane.OK_OPTION) {
                String cuentaDestino = campoCuentaDestino.getText();
                String montoStr = campoMonto.getText();

                try {
                    double monto = Double.parseDouble(montoStr);
                    boolean exito = controlador.transferirDinero(cuentaId, cuentaDestino, monto);
                    double saldo = controlador.consultarSaldo(cuentaId);

                    resultado.setText(exito ? "✅ Transferencia exitosa. Nuevo saldo: $" + saldo
                                            : "❌ Transferencia fallida. Saldo: $" + saldo);
                } catch (NumberFormatException ex) {
                    Mensajes.mostrarError("Monto inválido.");
                }
            }
        });

        btnHistorial.addActionListener(e -> {
            List<Transaccion> lista = HistorialTransacciones.obtenerHistorial();
            if (lista.isEmpty()) {
                Mensajes.mostrarMensaje("No hay transacciones registradas.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Transaccion t : lista) {
                    if (t.getNumeroCuenta().equals(cuentaId)) {
                        sb.append(t.toString()).append("\n");
                    }
                }
                if (sb.length() == 0) {
                    sb.append("No hay transacciones para esta cuenta.");
                }
                JTextArea area = new JTextArea(sb.toString());
                area.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(area);
                scrollPane.setPreferredSize(new Dimension(350, 200));
                JOptionPane.showMessageDialog(null, scrollPane, "Historial de Transacciones", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnConfiguracion.addActionListener(e -> {
            String nombre = controlador.obtenerNombre(cuentaId);
            double saldo = controlador.consultarSaldo(cuentaId);
            double limiteRetiro = controlador.obtenerLimiteRetiro(cuentaId);
            double limiteTransferencia = controlador.obtenerLimiteTransferencia(cuentaId);
            String preguntaActual = controlador.obtenerPreguntaSeguridad(cuentaId);
            String respuestaActual = controlador.obtenerRespuestaSeguridad(cuentaId); // corregido aquí

            String[] preguntas = {
                "¿Cuál es el nombre de tu mascota?",
                "¿Cuál es tu color favorito?",
                "¿En qué ciudad naciste?",
                "¿Cuál es tu comida favorita?",
                "¿Nombre de tu mejor amigo de infancia?"
            };

            JPanel panelConf = new JPanel(new GridLayout(0, 2));

            JTextField campoNombre = new JTextField(nombre);
            campoNombre.setEditable(true);

            JTextField campoId = new JTextField(cuentaId);
            campoId.setEditable(false);

            JTextField campoSaldo = new JTextField(String.valueOf(saldo));
            campoSaldo.setEditable(false);

            JTextField campoRetiro = new JTextField(String.valueOf(limiteRetiro));
            JTextField campoTransferencia = new JTextField(String.valueOf(limiteTransferencia));

            JComboBox<String> comboPregunta = new JComboBox<>(preguntas);
            comboPregunta.setSelectedItem(preguntaActual);
            JTextField campoRespuesta = new JTextField(respuestaActual);

            panelConf.add(new JLabel("Nombre:"));
            panelConf.add(campoNombre);
            panelConf.add(new JLabel("Número de cuenta:"));
            panelConf.add(campoId);
            panelConf.add(new JLabel("Saldo total:"));
            panelConf.add(campoSaldo);
            panelConf.add(new JLabel("Límite de retiro:"));
            panelConf.add(campoRetiro);
            panelConf.add(new JLabel("Límite de transferencia:"));
            panelConf.add(campoTransferencia);
            panelConf.add(new JLabel("Pregunta de seguridad:"));
            panelConf.add(comboPregunta);
            panelConf.add(new JLabel("Respuesta de seguridad:"));
            panelConf.add(campoRespuesta);

            // Botón para cambiar el PIN
            JButton btnCambiarPin = new JButton("Actualizar PIN");
            panelConf.add(new JLabel(""));
            panelConf.add(btnCambiarPin);

            btnCambiarPin.addActionListener(ev -> {
                boolean pinActualizado = false;

                while (!pinActualizado) {
                    JPanel panelPin = new JPanel(new GridLayout(0, 1));
                    JPasswordField nuevoPin1 = new JPasswordField();
                    JPasswordField nuevoPin2 = new JPasswordField();

                    panelPin.add(new JLabel("Nuevo PIN (4 dígitos):"));
                    panelPin.add(nuevoPin1);
                    panelPin.add(new JLabel("Confirma el nuevo PIN:"));
                    panelPin.add(nuevoPin2);

                    int confirmar = JOptionPane.showConfirmDialog(null, panelPin, "Cambiar PIN", JOptionPane.OK_CANCEL_OPTION);

                    if (confirmar != JOptionPane.OK_OPTION) {
                        break; // El usuario canceló
                    }

                    String pin1 = new String(nuevoPin1.getPassword());
                    String pin2 = new String(nuevoPin2.getPassword());

                    if (!pin1.matches("\\d{4}")) {
                        JOptionPane.showMessageDialog(null, "❌ El PIN debe tener exactamente 4 dígitos.");
                    } else if (!pin1.equals(pin2)) {
                        JOptionPane.showMessageDialog(null, "❌ Los PIN no coinciden.");
                    } else {
                        controlador.actualizarPIN(cuentaId, pin1);
                        JOptionPane.showMessageDialog(null, "✅ PIN actualizado correctamente.");
                        pinActualizado = true;
                    }
                }
            });

            int result = JOptionPane.showConfirmDialog(null, panelConf, "Configuración de cuenta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String nuevoNombre = campoNombre.getText().trim();
                    double nuevoLimiteRetiro = Double.parseDouble(campoRetiro.getText());
                    double nuevoLimiteTransferencia = Double.parseDouble(campoTransferencia.getText());
                    String nuevaPregunta = (String) comboPregunta.getSelectedItem();
                    String nuevaRespuesta = campoRespuesta.getText().trim();

                    if (nuevaPregunta.isEmpty() || nuevaRespuesta.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "❌ La pregunta y respuesta de seguridad no pueden estar vacías.");
                        return;
                    }

                    controlador.actualizarConfiguracionCuenta(cuentaId, nuevoNombre, nuevoLimiteRetiro, nuevoLimiteTransferencia);
                    controlador.actualizarPreguntaYRespuestaSeguridad(cuentaId, nuevaPregunta, nuevaRespuesta);
                    JOptionPane.showMessageDialog(null, "✅ Configuración actualizada exitosamente.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "❌ Error: valores numéricos inválidos.");
                }
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
