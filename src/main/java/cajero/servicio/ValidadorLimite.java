package cajero.servicio;

import cajero.modelo.Cuenta;

public class ValidadorLimite {

    public boolean validarRetiro(Cuenta cuenta, double monto) {
        return monto > 0 && monto <= cuenta.getLimiteRetiro() && monto <= cuenta.getSaldo();
    }

    public boolean validarTransferencia(Cuenta cuenta, double monto) {
        return monto > 0 && monto <= cuenta.getLimiteTransferencia() && monto <= cuenta.getSaldo();
    }
}
