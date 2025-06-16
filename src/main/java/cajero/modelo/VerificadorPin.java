package cajero.modelo;

public class VerificadorPin {

    private static final int MAX_INTENTOS = 3;

    public boolean verificar(Cuenta cuenta, String pinIngresado) {
        if (cuenta.estaBloqueada()) {
            return false;
        }

        if (cuenta.getPin().equals(pinIngresado)) {
            cuenta.reiniciarIntentos();
            return true;
        } else {
            cuenta.incrementarIntentosFallidos();

            if (cuenta.getIntentosFallidos() >= MAX_INTENTOS) {
                cuenta.bloquearCuenta();
            }
            return false;
        }
    }
}