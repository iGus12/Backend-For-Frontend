package cl.sanosysalvo.BFF.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DtoTest {

    @Test
    void debeCrearMascotaDTO() {
        MascotaDTO mascota = new MascotaDTO();

        assertThat(mascota).isNotNull();
    }

    @Test
    void debeCrearMascotadetalleDTO() {
        MascotadetalleDTO detalle = new MascotadetalleDTO();

        assertThat(detalle).isNotNull();
    }

    @Test
    void debeCrearReporteDTO() {
        ReporteDTO reporte = new ReporteDTO();

        assertThat(reporte).isNotNull();
    }
}