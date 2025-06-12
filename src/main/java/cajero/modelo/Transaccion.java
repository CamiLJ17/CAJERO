package cajero.modelo;

import java.time.LocalDateTime;

public class Transaccion {
    private String tipo; // Ej: "retiro", "depósito", "consulta"
    private double monto;
    private LocalDateTime fecha;
    private String numeroCuenta;

    public Transaccion(String tipo, double monto, String numeroCuenta) {
        this.tipo = tipo;
        this.monto = monto;
        this.numeroCuenta = numeroCuenta;
        this.fecha = LocalDateTime.now();
    }

    public String getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    @Override
    public String toString() {
        return "[" + fecha + "] " + tipo.toUpperCase() + " - $" + monto + " - Cuenta: " + numeroCuenta;
    }
}
