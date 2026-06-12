package cl.sanosysalvo.BFF.dto;

public class MascotaDTO {

    private String nombre;
    private String especie;
    private String raza;
    private Integer edad;
    private Long dueñoId;
    private String vacunas;
    private String ubicacion;
    private String estadoReporte;
    private String foto;

    public MascotaDTO() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }   

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Long getDueñoId() {
        return dueñoId;
    }

    public void setDueñoId(Long dueñoId) {
        this.dueñoId = dueñoId;
    }

    public String getVacunas() {
        return vacunas;
    }

    public void setVacunas(String vacunas) {
        this.vacunas = vacunas;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstadoReporte() {
        return estadoReporte;
    }

    public void setEstadoReporte(String estadoReporte) {
        this.estadoReporte = estadoReporte;
    }
    public String getFoto() {
    return foto;
    }

    public void setFoto(String foto) {
    this.foto = foto;
    }
}