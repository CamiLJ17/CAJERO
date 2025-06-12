package cajero.modelo;
import cajero.vista.Mensajes;

public class VerificadorPin {

    private static final int MAX_INTENTOS = 3;

    public boolean verificar(Cuenta cuenta, String pinIngresado) {
        if (cuenta.estaBloqueada()) {
            
            Mensajes.mostrarError("La cuenta está bloqueada.");
            return false;
        }

        if (cuenta.getPin().equals(pinIngresado)) {
            cuenta.reiniciarIntentos();
            return true;
        } else {
            cuenta.incrementarIntentosFallidos();
            Mensajes.mostrarAdvertencia("PIN incorrecto. Intentos fallidos: " + cuenta.getIntentosFallidos());

            if (cuenta.getIntentosFallidos() >= MAX_INTENTOS) {
                cuenta.bloquearCuenta();
                Mensajes.mostrarError("🔒 La cuenta ha sido bloqueada por exceso de intentos.");
            }
            return false;
        }
    }
}
