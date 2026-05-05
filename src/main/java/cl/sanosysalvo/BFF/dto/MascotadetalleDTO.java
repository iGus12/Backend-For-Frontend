package cl.sanosysalvo.BFF.dto;


import java.util.List;

public class MascotadetalleDTO {

    private String nombre;
    private String especie;
    private String raza;
    private Integer edad;
    private Long dueñoId;
    private String vacunas;
    private List<ReporteDTO> reportes;

    public MascotadetalleDTO() {
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

    public List<ReporteDTO> getReportes() {
        return reportes;
    }

    public void setReportes(List<ReporteDTO> reportes) {
        this.reportes = reportes;
    }
}
