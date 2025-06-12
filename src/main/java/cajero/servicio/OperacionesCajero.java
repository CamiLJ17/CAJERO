package cajero.servicio;

import cajero.modelo.Cuenta;

public class OperacionesCajero {

    public boolean retirar(Cuenta cuenta, double monto) {
        return cuenta.retirar(monto);
    }

    public void depositar(Cuenta cuenta, double monto) {
        cuenta.depositar(monto);
    }
}
