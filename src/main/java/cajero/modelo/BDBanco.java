package cajero.modelo;

import java.util.HashMap;
import java.util.Map;

public class BDBanco {
    private Map<String, Cliente> clientes = new HashMap<>();

    public BDBanco() {
        // Datos simulados
        Cuenta cuenta1 = new Cuenta("123456", 1000.0, "1234");
        Cliente cliente1 = new Cliente("Camila", cuenta1);
        clientes.put("123456", cliente1);
    }

    public Cliente consultarCliente(String numeroCuenta) {
        return clientes.get(numeroCuenta);
    }

    public boolean validarCuenta(String numeroCuenta) {
        return clientes.containsKey(numeroCuenta);
    }
}
