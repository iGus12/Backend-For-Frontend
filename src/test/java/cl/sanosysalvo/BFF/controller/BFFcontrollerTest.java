package cl.sanosysalvo.BFF.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cl.sanosysalvo.BFF.dto.MascotaDTO;
import cl.sanosysalvo.BFF.service.BFFservice;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest(BFFcontroller.class)
@AutoConfigureMockMvc(addFilters = false) // Apagamos la seguridad para el test
public class BFFcontrollerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BFFservice bffService;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/bff/mascotas/listar - Debería retornar lista del BFFService")
    void testListarMascotas() throws Exception {
        // Arrange
        MascotaDTO mascota = new MascotaDTO();
        // Si tu MascotaDTO no tiene setNombre, simplemente borra esta línea
        // mascota.setNombre("Simón"); 

        when(bffService.obtenerMascotas()).thenReturn(Arrays.asList(mascota));

        // Act & Assert
        mockMvc.perform(get("/api/bff/mascotas/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/bff/adopcion/catalogo - Debería simular llamada exitosa al MS_Adopcion")
    void testObtenerCatalogoAdopcion() throws Exception {
        // Arrange: Simulamos lo que devolvería el microservicio de Adopción (puerto 8086)
        Map<String, Object> rocco = new HashMap<>();
        rocco.put("nombre", "Rocco");
        rocco.put("ubicacion", "Puente Alto");
        
        List<Map<String, Object>> respuestaSimulada = Arrays.asList(rocco);

        when(restTemplate.getForObject("http://localhost:8086/api/adopcion/catalogo", List.class))
                .thenReturn(respuestaSimulada);

        // Act & Assert
        mockMvc.perform(get("/api/bff/adopcion/catalogo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Rocco"))
                .andExpect(jsonPath("$[0].ubicacion").value("Puente Alto"));
    }

    @Test
    @DisplayName("DELETE /api/bff/mascotas/eliminar/{id} - Falla por falta de permisos (Rol USER)")
    void testEliminarMascota_SinPermisos() throws Exception {
        // Act & Assert: Intentamos eliminar con un rol que no es ADMIN ni VETERINARIO
        mockMvc.perform(delete("/api/bff/mascotas/eliminar/1")
                .header("rol", "USER")) // Rol incorrecto
                .andExpect(status().isForbidden()) // 403
                .andExpect(content().string("No tienes permiso para eliminar mascotas"));
    }

    @Test
    @DisplayName("DELETE /api/bff/mascotas/eliminar/{id} - Pasa exitosamente con Rol ADMIN")
    void testEliminarMascota_ConPermisos() throws Exception {
        // Arrange: Simulamos que la llamada al microservicio de mascotas (puerto 8081) sale bien
        ResponseEntity<Void> respuestaOk = new ResponseEntity<>(HttpStatus.OK);
        
        when(restTemplate.exchange(
                eq("http://localhost:8081/api/mascotas/1"),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(),
                eq(Void.class)
        )).thenReturn(respuestaOk);

        // Act & Assert: Intentamos eliminar con el rol correcto
        mockMvc.perform(delete("/api/bff/mascotas/eliminar/1")
                .header("rol", "ADMIN")) // Rol con permisos
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/bff/mascotas/crear-admin - Crea exitosamente si es VETERINARIO")
    void testCrearMascotaAdmin() throws Exception {
        // Arrange
        Map<String, Object> datosMascota = new HashMap<>();
        datosMascota.put("nombre", "Simón");
        datosMascota.put("especie", "Gato");

        Map<String, Object> respuestaMsMascotas = new HashMap<>();
        respuestaMsMascotas.put("mensaje", "Creado con éxito");

        ResponseEntity<Map> respuestaOk = new ResponseEntity<>(respuestaMsMascotas, HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq("http://localhost:8081/api/mascotas/crear-admin"),
                any(),
                eq(Map.class)
        )).thenReturn(respuestaOk);

        // Act & Assert
        mockMvc.perform(post("/api/bff/mascotas/crear-admin")
                .header("rol", "VETERINARIO") // Rol con permisos
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datosMascota)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Creado con éxito"));
    }
}