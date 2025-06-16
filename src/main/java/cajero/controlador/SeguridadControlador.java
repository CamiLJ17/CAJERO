package cajero.controlador;

import cajero.modelo.Cuenta;
import cajero.modelo.BDBanco;
import cajero.modelo.VerificadorPin;
import cajero.servicio.BloqueoTarjeta;

public class SeguridadControlador {

    private BDBanco baseDatos;
    private VerificadorPin verificador;
    private BloqueoTarjeta bloqueo;

    public SeguridadControlador() {
        this.baseDatos = BDBanco.getInstancia();
        this.verificador = new VerificadorPin();
        this.bloqueo = new BloqueoTarjeta();
    }

    public boolean autenticar(String cuentaId, String pin) {
        var cliente = baseDatos.consultarCliente(cuentaId);

        if (cliente == null) {
            return false;
        }

        Cuenta cuenta = cliente.getCuenta();
        boolean acceso = verificador.verificar(cuenta, pin);

        return acceso;
    }
} 