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

        return operaciones.retirar(cuenta, monto);
    }

    public void ingresarDinero(String cuentaId, double monto) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente != null) {
            Cuenta cuenta = cliente.getCuenta();
            if (!cuenta.estaBloqueada()) {
                operaciones.depositar(cuenta, monto);
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

    public boolean comprarEntradas(String cuentaId, double costo) {
        Cliente cliente = baseDatos.consultarCliente(cuentaId);
        if (cliente == null) return false;

        Cuenta cuenta = cliente.getCuenta();
        if (cuenta.estaBloqueada()) return false;

        return operaciones.retirar(cuenta, costo);
    }
}
