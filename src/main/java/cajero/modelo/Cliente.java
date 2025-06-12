package cajero.modelo;

public class Cliente {
    private String nombre;
    private Cuenta cuenta;

    public Cliente(String nombre, Cuenta cuenta) {
        this.nombre = nombre;
        this.cuenta = cuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }
}
