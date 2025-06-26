package cajero.vista;

import cajero.controlador.CajeroControlador;
import cajero.modelo.Cliente;
import cajero.modelo.HistorialTransacciones;
import cajero.modelo.SalaCine;
import cajero.modelo.Transaccion;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.awt.event.*;

public class Cajero {

    private static CajeroControlador controlador = new CajeroControlador();
    private static final SalaCine salaCine = new SalaCine(Arrays.asList(
            "Cómo entrenar a tu dragón",
            "Destino final: Lazos de sangre",
            "Elio",
            "Exterminio: La evolución",
            "Lilo & Stitch",
            "Misión imposible: La sentencia final"));

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Cajero::crearInterfaz);
    }

    public static void crearInterfaz() {
        JFrame frame = new JFrame("Cajero Automático");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 380);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel titulo = new JLabel("Bienvenido a NICA-Bank");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Botones ---
        Dimension buttonSize = new Dimension(200, 35);

        JButton btnIngresar = new JButton("Iniciar Sesión");
        JButton btnCrearCliente = new JButton("Crear Cliente");
        JButton btnRecuperarPIN = new JButton("¿Olvidaste tu PIN?");

        for (JButton btn : new JButton[] { btnIngresar, btnCrearCliente, btnRecuperarPIN }) {
            btn.setMaximumSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setBackground(new Color(220, 220, 250)); // color suave
            btn.setFocusPainted(false);
        }

        JLabel mensaje = new JLabel("", SwingConstants.CENTER);
        mensaje.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mensaje.setForeground(Color.RED);
        mensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Número de Cuenta ---
        JLabel labelCuenta = new JLabel("Número de Cuenta:");
        labelCuenta.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField campoCuenta = new JTextField();
        campoCuenta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel cuentaPanel = new JPanel();
        cuentaPanel.setLayout(new BorderLayout());
        cuentaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cuentaPanel.add(labelCuenta, BorderLayout.WEST);

        // --- PIN ---
        JLabel labelPIN = new JLabel("PIN:");
        labelPIN.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPasswordField campoPIN = new JPasswordField();
        campoPIN.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        campoPIN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnIngresar.doClick(); // Hace como si se hubiera hecho clic
            }
        });

        JPanel pinPanel = new JPanel();
        pinPanel.setLayout(new BorderLayout());
        pinPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pinPanel.add(labelPIN, BorderLayout.WEST);

        // --- Agregar al panel principal ---
        panel.add(titulo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(cuentaPanel);
        panel.add(campoCuenta);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(pinPanel);
        panel.add(campoPIN);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(btnIngresar);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnCrearCliente);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnRecuperarPIN);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(mensaje);

        // Acción de Iniciar Sesión
        btnIngresar.addActionListener((ActionEvent e) -> {
            String cuenta = campoCuenta.getText().trim();
            String pin = new String(campoPIN.getPassword()).trim();

            Cliente cliente = controlador.obtenerCliente(cuenta);

            if (cliente == null) {
                Mensajes.mostrarError("Cuenta no encontrada.");
                return;
            }

            if (cliente.getCuenta().estaBloqueada()) {
                boolean deseaRecuperar = Mensajes.confirmar(
                        "Tu cuenta está bloqueada. ¿Deseas intentar recuperarla respondiendo tu pregunta de seguridad?");
                if (deseaRecuperar) {
                    btnRecuperarPIN.doClick();
                }
                return;
            }

            boolean acceso = controlador.iniciarSesion(cuenta, pin);
            if (acceso) {
                mostrarMenuPrincipal(cuenta);
                frame.dispose();
            } else {
                Mensajes.mostrarAdvertencia("PIN incorrecto. Intenta nuevamente.");
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
                    Mensajes.mostrarAdvertencia("Saldo inválido. Debe ser un número.");
                    return;
                }

                if (nombre.trim().isEmpty() || !pin.matches("\\d{4}")) {
                    Mensajes.mostrarAdvertencia(
                            "Datos inválidos. Asegúrate de ingresar un nombre y un PIN de 4 dígitos.");
                    return;
                }

                String preguntaSeleccionada = (String) comboPreguntas.getSelectedItem();
                String respuesta = campoRespuesta.getText().trim();

                if (respuesta.isEmpty()) {
                    Mensajes.mostrarAdvertencia("Debes ingresar una respuesta a la pregunta de seguridad.");
                    return;
                }

                // Llamar al controlador con la nueva info
                String nuevaCuentaId = controlador.registrarNuevoCliente(nombre, pin, saldo, preguntaSeleccionada,
                        respuesta);
                Mensajes.confirmar("Cliente creado exitosamente.\nNúmero de cuenta: " + nuevaCuentaId);
            }
        });

        btnRecuperarPIN.addActionListener(e -> {
            // Primer panel: pedir número de cuenta
            JPanel panelRecuperacion = new JPanel(new GridLayout(0, 1));
            JTextField campoCuentaRecuperacion = new JTextField();

            panelRecuperacion.add(new JLabel("Número de cuenta:"));
            panelRecuperacion.add(campoCuentaRecuperacion);

            int resultado = JOptionPane.showConfirmDialog(null, panelRecuperacion, "Recuperar PIN",
                    JOptionPane.OK_CANCEL_OPTION);

            if (resultado == JOptionPane.OK_OPTION) {
                String cuentaId = campoCuentaRecuperacion.getText().trim();
                String pregunta = controlador.obtenerPreguntaSeguridad(cuentaId);

                if (pregunta == null) {
                    Mensajes.mostrarAdvertencia("Cuenta no encontrada.");
                    return;
                }

                // Segundo panel: validar respuesta a la pregunta
                JPanel panelSeguridad = new JPanel(new GridLayout(0, 1));
                panelSeguridad.add(new JLabel("Pregunta de seguridad:"));
                panelSeguridad.add(new JLabel(pregunta));

                JTextField campoRespuesta = new JTextField();
                panelSeguridad.add(new JLabel("Tu respuesta:"));
                panelSeguridad.add(campoRespuesta);

                int confirmar = JOptionPane.showConfirmDialog(null, panelSeguridad, "Verificación de identidad",
                        JOptionPane.OK_CANCEL_OPTION);

                if (confirmar == JOptionPane.OK_OPTION) {
                    String respuesta = campoRespuesta.getText().trim();

                    if (!controlador.validarRespuestaSeguridad(cuentaId, respuesta)) {
                        Mensajes.mostrarAdvertencia("Respuesta incorrecta.");
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

                        int confirmacionFinal = JOptionPane.showConfirmDialog(null, panelNuevoPin,
                                "Establecer nuevo PIN", JOptionPane.OK_CANCEL_OPTION);

                        if (confirmacionFinal != JOptionPane.OK_OPTION) {
                            break;
                        }

                        String nuevoPin1 = new String(campoNuevoPin1.getPassword());
                        String nuevoPin2 = new String(campoNuevoPin2.getPassword());

                        if (!nuevoPin1.matches("\\d{4}")) {
                            Mensajes.mostrarAdvertencia("El PIN debe tener exactamente 4 dígitos.");
                        } else if (!nuevoPin1.equals(nuevoPin2)) {
                            Mensajes.mostrarAdvertencia("Los PIN ingresados no coinciden.");
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
        JFrame menuFrame = new JFrame("Menú Principal");
        menuFrame.setSize(380, 470);
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("Menú Principal");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension buttonSize = new Dimension(220, 35);
        Color softBlue = new Color(220, 220, 250);

        // Crear y estilizar los botones
        JButton btnSaldo = new JButton("💰 Consultar saldo");
        JButton btnRetiro = new JButton("💸 Retirar dinero");
        JButton btnDeposito = new JButton("🏦 Depositar dinero");
        JButton btnTransferencia = new JButton("🔁 Transferir dinero");
        JButton btnHistorial = new JButton("📜 Ver historial");
        JButton btnConfiguracion = new JButton("⚙️ Configuración");
        JButton btnTickets = new JButton("🎟️ Comprar Tickets");
        JButton btnSalir = new JButton("🚪 Salir");

        JButton[] botones = {
                btnSaldo, btnRetiro, btnDeposito,
                btnTransferencia, btnHistorial, btnConfiguracion, btnTickets, btnSalir
        };

        for (JButton btn : botones) {
            btn.setMaximumSize(new Dimension(220, 35));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setBackground(new Color(220, 220, 250)); // Igual al del login
            btn.setFocusPainted(false);
        }

        JLabel resultado = new JLabel("", SwingConstants.CENTER);
        resultado.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar al panel
        panel.add(titulo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        for (JButton btn : botones) {
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(resultado);

        // Listeners
        btnSaldo.addActionListener(e -> {
            double saldo = controlador.consultarSaldo(cuentaId);
            resultado.setText("Saldo actual: $" + saldo);
        });

        btnRetiro.addActionListener(e -> {
            String montoStr = JOptionPane.showInputDialog("Ingrese el monto a retirar:");
            if (montoStr != null) {
                try {
                    double monto = Double.parseDouble(montoStr);
                    boolean exito = controlador.retirarDinero(cuentaId, monto);
                    double saldo = controlador.consultarSaldo(cuentaId);
                    resultado.setText(exito ? "Retiro exitoso. Nuevo saldo: $" + saldo
                            : "Retiro fallido. Saldo: $" + saldo);
                } catch (NumberFormatException ex) {
                    Mensajes.mostrarError("Monto inválido.");
                }
            }
        });

        btnDeposito.addActionListener(e -> {
            String montoStr = JOptionPane.showInputDialog("Ingrese el monto a depositar:");
            if (montoStr != null) {
                try {
                    double monto = Double.parseDouble(montoStr);
                    controlador.ingresarDinero(cuentaId, monto);
                    double saldo = controlador.consultarSaldo(cuentaId);
                    resultado.setText("Depósito realizado. Nuevo saldo: $" + saldo);
                } catch (NumberFormatException ex) {
                    Mensajes.mostrarError("Monto inválido.");
                }
            }
        });

        btnTransferencia.addActionListener(e -> {
            JPanel panelTransferencia = new JPanel(new GridLayout(0, 1));
            JTextField campoCuentaDestino = new JTextField();
            JTextField campoMonto = new JTextField();

            panelTransferencia.add(new JLabel("Cuenta destino:"));
            panelTransferencia.add(campoCuentaDestino);
            panelTransferencia.add(new JLabel("Monto a transferir:"));
            panelTransferencia.add(campoMonto);

            int opcion = JOptionPane.showConfirmDialog(null, panelTransferencia, "Transferencia",
                    JOptionPane.OK_CANCEL_OPTION);

            if (opcion == JOptionPane.OK_OPTION) {
                String cuentaDestino = campoCuentaDestino.getText().trim();
                try {
                    double monto = Double.parseDouble(campoMonto.getText());
                    boolean exito = controlador.transferirDinero(cuentaId, cuentaDestino, monto);
                    double saldo = controlador.consultarSaldo(cuentaId);

                    resultado.setText(exito ? "Transferencia exitosa. Nuevo saldo: $" + saldo
                            : "Transferencia fallida. Saldo: $" + saldo);
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
                JOptionPane.showMessageDialog(null, scrollPane, "Historial de Transacciones",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnConfiguracion.addActionListener(e -> {
            String nombre = controlador.obtenerNombre(cuentaId);
            double saldo = controlador.consultarSaldo(cuentaId);
            double limiteRetiro = controlador.obtenerLimiteRetiro(cuentaId);
            double limiteTransferencia = controlador.obtenerLimiteTransferencia(cuentaId);
            String preguntaActual = controlador.obtenerPreguntaSeguridad(cuentaId);
            String respuestaActual = controlador.obtenerRespuestaSeguridad(cuentaId);

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

                    int confirmar = JOptionPane.showConfirmDialog(null, panelPin, "Cambiar PIN",
                            JOptionPane.OK_CANCEL_OPTION);

                    if (confirmar != JOptionPane.OK_OPTION) {
                        break; // El usuario canceló
                    }

                    String pin1 = new String(nuevoPin1.getPassword());
                    String pin2 = new String(nuevoPin2.getPassword());

                    if (!pin1.matches("\\d{4}")) {
                        Mensajes.mostrarAdvertencia(" El PIN debe tener exactamente 4 dígitos.");
                    } else if (!pin1.equals(pin2)) {
                        Mensajes.mostrarAdvertencia("Los PIN no coinciden.");
                    } else {
                        controlador.actualizarPIN(cuentaId, pin1);
                        Mensajes.confirmar(" PIN actualizado correctamente.");
                        pinActualizado = true;
                    }
                }
            });

            int result = JOptionPane.showConfirmDialog(null, panelConf, "Configuración de cuenta",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String nuevoNombre = campoNombre.getText().trim();
                    double nuevoLimiteRetiro = Double.parseDouble(campoRetiro.getText());
                    double nuevoLimiteTransferencia = Double.parseDouble(campoTransferencia.getText());
                    String nuevaPregunta = (String) comboPregunta.getSelectedItem();
                    String nuevaRespuesta = campoRespuesta.getText().trim();

                    if (nuevaPregunta.isEmpty() || nuevaRespuesta.isEmpty()) {
                        Mensajes.mostrarAdvertencia(" La pregunta y respuesta de seguridad no pueden estar vacías.");
                        return;
                    }

                    controlador.actualizarConfiguracionCuenta(cuentaId, nuevoNombre, nuevoLimiteRetiro,
                            nuevoLimiteTransferencia);
                    controlador.actualizarPreguntaYRespuestaSeguridad(cuentaId, nuevaPregunta, nuevaRespuesta);
                    Mensajes.confirmar("Configuración actualizada exitosamente.");
                } catch (NumberFormatException ex) {
                    Mensajes.mostrarAdvertencia("Error: valores numéricos inválidos.");
                }
            }
        });

btnTickets.addActionListener(e -> {
    String[] peliculas = {
        "Cómo entrenar a tu dragón",
        "Destino final: Lazos de sangre",
        "Elio",
        "Exterminio: La evolución",
        "Lilo & Stitch",
        "Misión imposible: La sentencia final"
    };

    // Panel principal
    JPanel panelCompra = new JPanel();
    panelCompra.setLayout(new BoxLayout(panelCompra, BoxLayout.Y_AXIS));
    panelCompra.setBackground(Color.WHITE);
    panelCompra.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15)); 

    Font fuenteLabel = new Font("Segoe UI", Font.BOLD, 14);

    // Película
    JLabel labelPeli = new JLabel("Película:");
    labelPeli.setFont(fuenteLabel);
    labelPeli.setAlignmentX(Component.LEFT_ALIGNMENT);

    JComboBox<String> comboPeliculas = new JComboBox<>(peliculas);
    comboPeliculas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    comboPeliculas.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Localidad
    JLabel labelLocalidad = new JLabel("Localidad:");
    labelLocalidad.setFont(fuenteLabel);
    labelLocalidad.setAlignmentX(Component.LEFT_ALIGNMENT);

    JComboBox<String> comboLocalidad = new JComboBox<>(new String[] { "General", "Preferencial" });
    comboLocalidad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    comboLocalidad.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Cantidad
    JLabel labelCantidad = new JLabel("Número de entradas:");
    labelCantidad.setFont(fuenteLabel);
    labelCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);

    JTextField campoCantidad = new JTextField();
    campoCantidad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    campoCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Botón pagar
    JButton btnPagar = new JButton("Pagar");
    btnPagar.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnPagar.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnPagar.setForeground(Color.BLACK);
    btnPagar.setMaximumSize(new Dimension(150, 35));
    btnPagar.setBackground(new Color(220, 220, 250)); // Igual al del login
    btnPagar.setFocusPainted(false);
    btnPagar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnPagar.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(150, 150, 200), 1),
        BorderFactory.createEmptyBorder(8, 25, 8, 25)
    ));



    // Agregamos al panel
    panelCompra.add(labelPeli);
    panelCompra.add(Box.createVerticalStrut(5));
    panelCompra.add(comboPeliculas);
    panelCompra.add(Box.createVerticalStrut(15));

    panelCompra.add(labelLocalidad);
    panelCompra.add(Box.createVerticalStrut(5));
    panelCompra.add(comboLocalidad);
    panelCompra.add(Box.createVerticalStrut(15));

    panelCompra.add(labelCantidad);
    panelCompra.add(Box.createVerticalStrut(5));
    panelCompra.add(campoCantidad);
    panelCompra.add(Box.createVerticalStrut(25));

    panelCompra.add(btnPagar);

    // Mostrar diálogo
    JDialog dialogo = new JDialog();
    dialogo.setTitle("Compra de Tickets");
    dialogo.setModal(true);
    dialogo.getContentPane().add(panelCompra);
    dialogo.pack();
    dialogo.setLocationRelativeTo(null);

    // Acción del botón pagar
    btnPagar.addActionListener(ev -> {
        String pelicula = (String) comboPeliculas.getSelectedItem();
        String localidad = (String) comboLocalidad.getSelectedItem();
        String textoCantidad = campoCantidad.getText().trim();

        try {
            int cantidad = Integer.parseInt(textoCantidad);
            if (cantidad <= 0) {
                Mensajes.mostrarAdvertencia("La cantidad debe ser mayor que 0.");
                return;
            }

            int disponibles = salaCine.sillasDisponibles(pelicula, localidad);
            if (cantidad > disponibles) {
                Mensajes.mostrarAdvertencia(
                    "No hay suficientes sillas disponibles en " + localidad + " para esta película.");
                return;
            }

            int precio = localidad.equals("General") ? 10000 : 15000;
            double montoTotal = cantidad * precio;

            boolean exito = controlador.comprarTickets(cuentaId, montoTotal);
            if (exito) {
                List<String> asignadas = salaCine.asignarSillas(pelicula, localidad, cantidad);
                if (asignadas.isEmpty()) {
                    Mensajes.mostrarAdvertencia("Error interno al asignar las sillas.");
                    return;
                }

                String mensaje = "Compra exitosa para '" + pelicula + "'. Entradas asignadas: "
                        + String.join(", ", asignadas);
                Mensajes.confirmar(mensaje);
                dialogo.dispose();
            } else {
                Mensajes.mostrarAdvertencia("No fue posible realizar la compra. Verifica tu saldo o estado de la cuenta.");
            }

        } catch (NumberFormatException ex) {
            Mensajes.mostrarAdvertencia("Ingrese un número válido de entradas.");
        }
    });

    dialogo.setVisible(true);
});

        btnSalir.addActionListener(e -> {
            menuFrame.dispose();
            crearInterfaz(); 
        });

        menuFrame.add(panel);
        menuFrame.setVisible(true);

    }
}
