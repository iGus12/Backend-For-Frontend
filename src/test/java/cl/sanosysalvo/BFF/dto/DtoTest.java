package cl.sanosysalvo.BFF.dto;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class DtoTest {

    @Test
    @DisplayName("MascotaDTO - Debería guardar y retornar correctamente los datos de Rocco")
    void debeValidarGettersYSettersDeMascotaDTO() {
        // Arrange
        MascotaDTO mascota = new MascotaDTO();
        
        mascota.setNombre("Rocco");
        mascota.setEspecie("Perro");
        mascota.setRaza("Pastor Alemán ");

        assertThat(mascota).isNotNull();
        assertThat(mascota.getNombre()).isEqualTo("Rocco");
        assertThat(mascota.getEspecie()).isEqualTo("Perro");
        assertThat(mascota.getRaza()).isEqualTo("Pastor Alemán ");
    }

    @Test
    @DisplayName("MascotadetalleDTO - Debería armar el detalle completo de Simón con sus reportes")
    void debeValidarMascotadetalleDTOConReportes() {
        // Arrange
        MascotadetalleDTO detalle = new MascotadetalleDTO();
        ReporteDTO reporteUbicacion = new ReporteDTO();
        // Nota: Si ReporteDTO tiene un setTipo o setUbicacion, podrías agregarlo aquí
        
        List<ReporteDTO> listaReportes = Arrays.asList(reporteUbicacion);

        // Act
        detalle.setNombre("Simón");
        detalle.setEspecie("Gato");
        detalle.setRaza("Romano");
        detalle.setReportes(listaReportes);

        // Assert
        assertThat(detalle).isNotNull();
        assertThat(detalle.getNombre()).isEqualTo("Simón");
        assertThat(detalle.getRaza()).isEqualTo("Romano");
        assertThat(detalle.getReportes()).hasSize(1);
        assertThat(detalle.getReportes()).contains(reporteUbicacion);
    }

    @Test
    @DisplayName("ReporteDTO - Debería instanciarse y manejar datos básicos")
    void debeValidarReporteDTO() {
        // Arrange & Act
        ReporteDTO reporte = new ReporteDTO();
        assertThat(reporte).isNotNull();
    }
}