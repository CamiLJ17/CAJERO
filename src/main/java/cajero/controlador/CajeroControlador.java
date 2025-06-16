package cajero.controlador;

import cajero.modelo.*;
import cajero.servicio.*;
import cajero.vista.Mensajes;

public class CajeroControlador {
    private BDBanco baseDatos;
    private VerificadorPin verificador;
    private OperacionesCajero operaciones;
    private ValidadorLimite validador;

    public CajeroControlador() {
        this.baseDatos = new BDBanco();
        this.verificador = new VerificadorPin();
        this.operaciones = new OperacionesCajero();
        this.validador = new ValidadorLimite();
    }

    public boolean iniciarSesion(String cuentaId, String pin) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente == null) {
            Mensajes.mostrarAdvertencia("Cuenta no encontrada, verifique su número de cuenta");
            return false;
        }
        return verificador.verificar(cliente.getCuenta(), pin);
    }

    public boolean retirarDinero(String cuentaId, double monto) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente == null) return false;

        Cuenta cuenta = cliente.getCuenta();
        if (cuenta.estaBloqueada()) return false;

        if (!validador.validarRetiro(cuenta, monto)) return false;

        boolean exito = operaciones.retirar(cuenta, monto);
        if (exito) {
            HistorialTransacciones.registrar(new Transaccion("Retiro", monto, cuentaId));
        }
        return exito;
    }

    public void ingresarDinero(String cuentaId, double monto) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente != null) {
            Cuenta cuenta = cliente.getCuenta();
            if (!cuenta.estaBloqueada()) {
                operaciones.depositar(cuenta, monto);
                HistorialTransacciones.registrar(new Transaccion("Depósito", monto, cuentaId));
            }
        }
    }

    public double consultarSaldo(String cuentaId) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente != null) {
            return cliente.getCuenta().getSaldo();
        }
        return -1;
    }

    public boolean transferirDinero(String cuentaOrigenId, String cuentaDestinoId, double monto) {
        if (cuentaOrigenId.equals(cuentaDestinoId)) {
            Mensajes.mostrarAdvertencia("No puedes transferirte dinero a ti mismo.");
            return false;
        }

        Cliente clienteOrigen = baseDatos.consultarCliente(cuentaOrigenId);
        Cliente clienteDestino = baseDatos.consultarCliente(cuentaDestinoId);

        if (clienteOrigen == null || clienteDestino == null) {
            Mensajes.mostrarError("Cuenta de origen o destino no válida.");
            return false;
        }

        Cuenta origen = clienteOrigen.getCuenta();
        Cuenta destino = clienteDestino.getCuenta();

        if (origen.estaBloqueada()) {
            Mensajes.mostrarAdvertencia("La cuenta de origen está bloqueada.");
            return false;
        }

        if (!validador.validarTransferencia(origen, monto)) {
            Mensajes.mostrarAdvertencia("Fondos insuficientes o monto inválido.");
            return false;
        }

        boolean exito = operaciones.retirar(origen, monto);
        if (exito) {
            operaciones.depositar(destino, monto);

            // Registro de transacciones mejorado:
            HistorialTransacciones.registrar(new Transaccion(
                "Transferencia enviada a " + cuentaDestinoId, monto, cuentaOrigenId));

            HistorialTransacciones.registrar(new Transaccion(
                "Transferencia recibida de " + cuentaOrigenId, monto, cuentaDestinoId));

            Mensajes.mostrarMensaje("✅ Transferencia realizada exitosamente a la cuenta " + cuentaDestinoId);
            return true;
        } else {
            Mensajes.mostrarError("No se pudo completar la transferencia.");
            return false;
        }
    }
    
    public String registrarNuevoCliente(String nombre, String pin, double saldoInicial, String pregunta, String respuesta) {
        if (pin == null || pin.length() != 4 || !pin.matches("\\d{4}")) {
            Mensajes.mostrarAdvertencia("El PIN debe tener exactamente 4 dígitos.");
            return null;
        }

        if (saldoInicial < 0) {
            Mensajes.mostrarAdvertencia("El saldo inicial no puede ser negativo.");
            return null;
        }

        String nuevoId = baseDatos.agregarCliente(nombre, pin, saldoInicial, pregunta, respuesta);
        Mensajes.mostrarMensaje("✅ Cliente creado exitosamente. Número de cuenta: " + nuevoId);
        return nuevoId;
    }

    public boolean recuperarContrasena(String cuentaId, String respuestaIngresada, String nuevoPIN1, String nuevoPIN2) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente == null) {
            Mensajes.mostrarError("❌ Cuenta no encontrada.");
            return false;
        }

        String respuestaGuardada = cliente.getRespuestaSeguridad();
        if (!respuestaGuardada.equalsIgnoreCase(respuestaIngresada.trim())) {
            Mensajes.mostrarAdvertencia("⚠️ Respuesta incorrecta.");
            return false;
        }

        if (!nuevoPIN1.equals(nuevoPIN2)) {
            Mensajes.mostrarAdvertencia("⚠️ Los PIN ingresados no coinciden.");
            return false;
        }

        if (!nuevoPIN1.matches("\\d{4}")) {
            Mensajes.mostrarAdvertencia("⚠️ El nuevo PIN debe tener exactamente 4 dígitos.");
            return false;
        }

        cliente.getCuenta().setPin(nuevoPIN1);
        Mensajes.mostrarMensaje("✅ PIN actualizado exitosamente.");
        return true;
    }

    public String obtenerPreguntaSeguridad(String cuentaId) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        return (cliente != null) ? cliente.getPreguntaSeguridad() : null;
    }
    
    public boolean validarRespuestaSeguridad(String cuentaId, String respuesta) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        return cliente != null && cliente.getRespuestaSeguridad().equalsIgnoreCase(respuesta);
    }

    public String obtenerRespuestaSeguridad(String cuentaId) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        return (cliente != null) ? cliente.getRespuestaSeguridad() : "";
    }


    public void actualizarPreguntaYRespuestaSeguridad(String cuentaId, String nuevaPregunta, String nuevaRespuesta) {
    Cliente cliente = baseDatos.consultarCliente(cuentaId);
    if (cliente != null) {
        cliente.setPreguntaSeguridad(nuevaPregunta);
        cliente.setRespuestaSeguridad(nuevaRespuesta);
    }
}


    // Obtener el nombre del cliente
    public String obtenerNombre(String cuentaId) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        return (cliente != null) ? cliente.getNombre() : "";
    }

    // Obtener el límite de retiro
    public double obtenerLimiteRetiro(String cuentaId) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente != null) {
            return cliente.getCuenta().getLimiteRetiro();
        }
        return 0.0;
    }

    // Obtener el límite de transferencia
    public double obtenerLimiteTransferencia(String cuentaId) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente != null) {
            return cliente.getCuenta().getLimiteTransferencia();
        }
        return 0.0;
    }

    // Actualizar la configuración de la cuenta
    public void actualizarConfiguracionCuenta(String cuentaId, String nuevoNombre, double nuevoLimiteRetiro, double nuevoLimiteTransferencia) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente != null) {
            cliente.setNombre(nuevoNombre);
            cliente.getCuenta().setLimiteRetiro(nuevoLimiteRetiro);
            cliente.getCuenta().setLimiteTransferencia(nuevoLimiteTransferencia);
        }
    }

    public void actualizarPIN(String cuentaId, String nuevoPIN) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente != null) {
            cliente.getCuenta().setPin(nuevoPIN);
        }
    }

}
