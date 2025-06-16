package cajero.modelo;

public class Cliente {
    private String nombre;
    private Cuenta cuenta;

    private String preguntaSeguridad;
    private String respuestaSeguridad;

    public Cliente(String nombre, Cuenta cuenta, String preguntaSeguridad, String respuestaSeguridad) {
        this.nombre = nombre;
        this.cuenta = cuenta;
        this.preguntaSeguridad = preguntaSeguridad;
        this.respuestaSeguridad = respuestaSeguridad.toLowerCase().trim(); 
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public String getPreguntaSeguridad() {
        return preguntaSeguridad;
    }

    public String getRespuestaSeguridad() {
        return respuestaSeguridad;
    }

    public void setPreguntaSeguridad(String preguntaSeguridad) {
        this.preguntaSeguridad = preguntaSeguridad;
    }

    public void setRespuestaSeguridad(String respuestaSeguridad) {
        this.respuestaSeguridad = respuestaSeguridad.toLowerCase().trim();
    }
}
