package cl.sanosysalvo.BFF.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import cl.sanosysalvo.BFF.client.MascotaCliente;
import cl.sanosysalvo.BFF.client.ReporteClient;
import cl.sanosysalvo.BFF.dto.MascotaDTO;
import cl.sanosysalvo.BFF.dto.MascotadetalleDTO;
import cl.sanosysalvo.BFF.dto.ReporteDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class BFFserviceTest {
    @Mock
    private MascotaCliente mascotaClient;

    @Mock
    private ReporteClient reporteClient;

    @InjectMocks
    private BFFservice bffService;

    @Test
    @DisplayName("Debería obtener el detalle de Simón juntando datos de Mascotas y Reportes")
    void testObtenerDetalleMascota_ConReportesExitoso() {
        // Arrange
        MascotaDTO simon = new MascotaDTO();
        simon.setNombre("Simón");
        simon.setEspecie("Gato");
        simon.setRaza("Romano");
        
        ReporteDTO reporteSede = new ReporteDTO();
        List<ReporteDTO> reportesSimulados = Arrays.asList(reporteSede);

        when(mascotaClient.obtenerMascotaPorId(1L)).thenReturn(simon);
        when(reporteClient.obtenerReportesPorMascota(1L)).thenReturn(reportesSimulados);

        // Act
        MascotadetalleDTO resultado = bffService.obtenerDetalleMascota(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Simón", resultado.getNombre());
        assertEquals("Romano", resultado.getRaza());
        assertEquals(1, resultado.getReportes().size()); 
    }

    @Test
    @DisplayName("Debería retornar a Rocco sin reportes si el ms_reportes falla (Prueba de Resiliencia)")
    void testObtenerDetalleMascota_FalloEnReportes() {
        // Arrange
        MascotaDTO rocco = new MascotaDTO();
        rocco.setNombre("Rocco");
        rocco.setEspecie("Perro");

        when(mascotaClient.obtenerMascotaPorId(2L)).thenReturn(rocco);
       
        when(reporteClient.obtenerReportesPorMascota(2L)).thenThrow(new RuntimeException("Timeout de conexión"));

        // Act
        MascotadetalleDTO resultado = bffService.obtenerDetalleMascota(2L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Rocco", resultado.getNombre());
        assertTrue(resultado.getReportes().isEmpty()); 
    }

    @Test
    @DisplayName("Debería listar todas las mascotas del sistema")
    void testObtenerMascotas() {
        // Arrange
        MascotaDTO m1 = new MascotaDTO();
        m1.setNombre("Simón");
        
        MascotaDTO m2 = new MascotaDTO();
        m2.setNombre("Rocco");

        when(mascotaClient.listarTodas()).thenReturn(Arrays.asList(m1, m2));

        // Act
        List<MascotaDTO> resultado = bffService.obtenerMascotas();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Simón", resultado.get(0).getNombre());
        assertEquals("Rocco", resultado.get(1).getNombre());
        verify(mascotaClient, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Debería crear un reporte usando el cliente de mascotas")
    void testCrearReporte() {
        // Arrange
        MascotaDTO simon = new MascotaDTO();
        simon.setNombre("Simón");

        when(mascotaClient.enviarReporte(any(MascotaDTO.class), eq("URGENTE"))).thenReturn(simon);

        // Act
        MascotaDTO resultado = bffService.crearReporte(simon, "URGENTE");

        // Assert
        assertNotNull(resultado);
        assertEquals("Simón", resultado.getNombre());
        verify(mascotaClient, times(1)).enviarReporte(simon, "URGENTE");
    }
}