package cajero.servicio;

import cajero.modelo.Cuenta;

public class OperacionesCajero {

    public boolean retirar(Cuenta cuenta, double monto) {
        return cuenta.retirar(monto);
    }

    public void depositar(Cuenta cuenta, double monto) {
        cuenta.depositar(monto);
    }
    
    public boolean transferir(Cuenta origen, Cuenta destino, double monto) {
    if (origen == null || destino == null) return false;
    if (origen.estaBloqueada() || destino.estaBloqueada()) return false;
    if (origen.retirar(monto)) {
        destino.depositar(monto);
        return true;
    }
    return false;
}

}
