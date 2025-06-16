package cajero.modelo;

import java.util.HashMap;
import java.util.Map;

public class BDBanco {
    private Map<String, Cliente> clientes = new HashMap<>();
    private int contadorCuentas = 100001; 

    public BDBanco() {
        // Cuenta 1
        Cuenta cuenta1 = new Cuenta("123456", 1000.0, "1234");
        Cliente cliente1 = new Cliente("Camila", cuenta1, "¿Cuál es tu color favorito?", "rojo");
        clientes.put("123456", cliente1);
                
        // Cuenta 2
        Cuenta cuenta2 = new Cuenta("654321", 500.0, "4321");
        Cliente cliente2 = new Cliente("Dani", cuenta2, "¿Cuál es el nombre de tu mascota?", "vivaldi");
        clientes.put("654321", cliente2);
    }

    public Cliente consultarCliente(String numeroCuenta) {
        return clientes.get(numeroCuenta);
    }

    public boolean validarCuenta(String numeroCuenta) {
        return clientes.containsKey(numeroCuenta);
    }

    // Método para agregar un nuevo cliente con ID generado automáticamente
    public String agregarCliente(String nombre, String pin, double saldoInicial, String pregunta, String respuesta) {
        String nuevoId = generarNuevoIdCuenta();
        Cuenta nuevaCuenta = new Cuenta(nuevoId, saldoInicial, pin);
        Cliente nuevoCliente = new Cliente(nombre, nuevaCuenta, pregunta, respuesta);
        clientes.put(nuevoId, nuevoCliente);
        return nuevoId;
    }



    // Generador de número de cuenta único
    private String generarNuevoIdCuenta() {
        return String.valueOf(contadorCuentas++);
    }
}
