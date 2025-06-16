package cajero.servicio;

import cajero.modelo.Cuenta;

public class BloqueoTarjeta {

    public void intentarBloqueo(Cuenta cuenta) {
        if (cuenta != null && !cuenta.estaBloqueada()) {
            cuenta.bloquear();
        }
    }
} 
