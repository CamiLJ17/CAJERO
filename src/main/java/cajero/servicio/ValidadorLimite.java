package cajero.servicio;

import cajero.modelo.Cuenta;

public class ValidadorLimite {

    private static final double LIMITE_RETIRO = 500.0;

    public boolean validarRetiro(Cuenta cuenta, double monto) {
        return monto > 0 && monto <= LIMITE_RETIRO && monto <= cuenta.getSaldo();
    }
}
