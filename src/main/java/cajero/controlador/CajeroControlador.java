package cajero.controlador;

import cajero.modelo.*;
import cajero.servicio.*;

public class CajeroControlador {
    private BDBanco baseDatos;
    private VerificadorPin verificador;
    private OperacionesCajero operaciones;
    private ValidadorLimite validador;

    public CajeroControlador() {
        this.baseDatos = BDBanco.getInstancia(); 

        this.verificador = new VerificadorPin();
        this.operaciones = new OperacionesCajero();
        this.validador = new ValidadorLimite();
    }

    public boolean iniciarSesion(String cuentaId, String pin) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente == null) return false;
        return verificador.verificar(cliente.getCuenta(), pin);
    }

    public Cliente obtenerCliente(String cuentaId) {
    return baseDatos.consultarCliente(cuentaId);
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

        public boolean comprarTickets(String cuentaId, double monto) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente == null) return false;

        Cuenta cuenta = cliente.getCuenta();
        if (cuenta.estaBloqueada()) return false;

        boolean exito = operaciones.comprarTickets(cuenta, monto);
        if (exito) {
            HistorialTransacciones.registrar(new Transaccion("Compra de Tickets", monto, cuentaId));
        }
        return exito;
    }

    public boolean ingresarDinero(String cuentaId, double monto) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente != null) {
            Cuenta cuenta = cliente.getCuenta();
            if (!cuenta.estaBloqueada()) {
                operaciones.depositar(cuenta, monto);
                HistorialTransacciones.registrar(new Transaccion("Depósito", monto, cuentaId));
                return true;
            }
        }
        return false;
    }

    public double consultarSaldo(String cuentaId) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente != null) {
            return cliente.getCuenta().getSaldo();
        }
        return -1;
    }

    public boolean transferirDinero(String cuentaOrigenId, String cuentaDestinoId, double monto) {
        if (cuentaOrigenId.equals(cuentaDestinoId)) return false;

        Cliente clienteOrigen = baseDatos.consultarCliente(cuentaOrigenId);
        Cliente clienteDestino = baseDatos.consultarCliente(cuentaDestinoId);

        if (clienteOrigen == null || clienteDestino == null) return false;

        Cuenta origen = clienteOrigen.getCuenta();
        Cuenta destino = clienteDestino.getCuenta();

        if (origen.estaBloqueada()) return false;

        if (!validador.validarTransferencia(origen, monto)) return false;

        boolean exito = operaciones.retirar(origen, monto);
        if (exito) {
            operaciones.depositar(destino, monto);

            HistorialTransacciones.registrar(new Transaccion(
                "Transferencia enviada a " + cuentaDestinoId, monto, cuentaOrigenId));

            HistorialTransacciones.registrar(new Transaccion(
                "Transferencia recibida de " + cuentaOrigenId, monto, cuentaDestinoId));

            return true;
        }
        return false;
    }

    public String registrarNuevoCliente(String nombre, String pin, double saldoInicial, String pregunta, String respuesta) {
        if (pin == null || pin.length() != 4 || !pin.matches("\\d{4}")) return null;

        if (saldoInicial < 0) return null;

        return baseDatos.agregarCliente(nombre, pin, saldoInicial, pregunta, respuesta);
    }

    public boolean recuperarContrasena(String cuentaId, String respuestaIngresada, String nuevoPIN1, String nuevoPIN2) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente == null) return false;

        String respuestaGuardada = cliente.getRespuestaSeguridad();
        if (!respuestaGuardada.equalsIgnoreCase(respuestaIngresada.trim())) return false;

        if (!nuevoPIN1.equals(nuevoPIN2)) return false;

        if (!nuevoPIN1.matches("\\d{4}")) return false;

        cliente.getCuenta().setPin(nuevoPIN1);
        cliente.getCuenta().desbloquear(); 
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

    public String obtenerNombre(String cuentaId) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        return (cliente != null) ? cliente.getNombre() : "";
    }

    public double obtenerLimiteRetiro(String cuentaId) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente != null) {
            return cliente.getCuenta().getLimiteRetiro();
        }
        return 0.0;
    }

    public double obtenerLimiteTransferencia(String cuentaId) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente != null) {
            return cliente.getCuenta().getLimiteTransferencia();
        }
        return 0.0;
    }

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
