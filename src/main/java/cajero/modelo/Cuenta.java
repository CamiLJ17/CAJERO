package cajero.modelo;

public class Cuenta {
    private String numeroCuenta;
    private double saldo;
    private String pin;
    private boolean bloqueada = false;

    private int intentosFallidos = 0;
    private double limiteRetiro = 500.0;
    private double limiteTransferencia = 1000.0;

    public Cuenta(String numeroCuenta, double saldo, String pin) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.pin = pin;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public boolean estaBloqueada() {
        return bloqueada;
    }

    public void bloquear() {
        this.bloqueada = true;
    }

    public void desbloquear() {
        this.bloqueada = false;
        this.intentosFallidos = 0;
    }


    public void bloquearCuenta() {
        bloquear(); 
    }

    public boolean verificarPIN(String inputPin) {
        return this.pin.equals(inputPin);
    }

    public String getPin() {
        return pin;
    }
    
    public void setPin(String nuevoPin) {
        if (nuevoPin != null && nuevoPin.matches("\\d{4}")) {
            this.pin = nuevoPin;
        }
    }

    public double getLimiteRetiro() {
        return limiteRetiro;
    }

    public void setLimiteRetiro(double nuevoLimite) {
        if (nuevoLimite > 0) {
            this.limiteRetiro = nuevoLimite;
        }
    }

    public double getLimiteTransferencia() {
        return limiteTransferencia;
    }

    public void setLimiteTransferencia(double nuevoLimite) {
        if (nuevoLimite > 0) {
            this.limiteTransferencia = nuevoLimite;
        }
    }

    public void depositar(double cantidad) {
        if (!bloqueada && cantidad > 0) {
            this.saldo += cantidad;
        }
    }

    public boolean retirar(double cantidad) {
        if (!bloqueada && cantidad > 0 && cantidad <= saldo) {
            this.saldo -= cantidad;
            return true;
        }
        return false;
    }

    // Métodos para manejo de intentos fallidos
    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void incrementarIntentosFallidos() {
        intentosFallidos++;
    }

    public void reiniciarIntentos() {
        intentosFallidos = 0;
    }
}
