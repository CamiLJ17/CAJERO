package cajero.modelo;

import java.util.ArrayList;
import java.util.List;

public class Banco {
    private String nombre;
    private List<Cliente> clientes;

    public Banco(String nombre) {
        this.nombre = nombre;
        this.clientes = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void agregarCliente(Cliente cliente) {
        clientes.add(cliente);
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public Cliente buscarClientePorCuenta(String numeroCuenta) {
        for (Cliente cliente : clientes) {
            if (cliente.getCuenta().getNumeroCuenta().equals(numeroCuenta)) {
                return cliente;
            }
        }
        return null;
    }
}
