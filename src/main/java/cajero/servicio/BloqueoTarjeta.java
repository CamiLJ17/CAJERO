package cajero.servicio;

import cajero.modelo.Cuenta;
import cajero.vista.Mensajes;

public class BloqueoTarjeta {

    public void intentarBloqueo(Cuenta cuenta) {
        if (cuenta != null && !cuenta.estaBloqueada()) {
            cuenta.bloquear();
            Mensajes.mostrarError("🚫 Tarjeta bloqueada por seguridad.");
        }
    }
}
