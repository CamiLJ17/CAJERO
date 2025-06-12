package cajero.controlador;

import cajero.modelo.Cuenta;
import cajero.modelo.BDBanco;
import cajero.modelo.VerificadorPin;
import cajero.servicio.BloqueoTarjeta;
import cajero.vista.Mensajes;

public class SeguridadControlador {

    private BDBanco baseDatos;
    private VerificadorPin verificador;
    private BloqueoTarjeta bloqueo;

    public SeguridadControlador() {
        this.baseDatos = new BDBanco();
        this.verificador = new VerificadorPin();
        this.bloqueo = new BloqueoTarjeta();
    }

    public boolean autenticar(String cuentaId, String pin) {
        Cuenta cuenta = baseDatos.consultarCliente(cuentaId).getCuenta();

        if (cuenta == null) {
            Mensajes.mostrarAdvertencia("Cuenta no encontrada, verifique su número de cuenta");
            return false;
        }

        boolean acceso = verificador.verificar(cuenta, pin);
        if (!acceso) {
            if (cuenta.estaBloqueada()) {
                Mensajes.mostrarError("🚫 Cuenta bloqueada. Contacte con su banco.");
            } else {
                Mensajes.mostrarAdvertencia("❗ PIN incorrecto.");
            }
        }

        return acceso;
    }
}
