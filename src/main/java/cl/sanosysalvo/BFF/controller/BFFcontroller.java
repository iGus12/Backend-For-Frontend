package cl.sanosysalvo.BFF.controller;

import cl.sanosysalvo.BFF.dto.MascotaDTO;
import cl.sanosysalvo.BFF.dto.MascotadetalleDTO; 
import cl.sanosysalvo.BFF.service.BFFservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate; 

import java.util.List;
import java.util.Map;       
import java.util.HashMap;   

@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "*") 
public class BFFcontroller {

    @Autowired
    private BFFservice bffService;

    @Autowired
    private RestTemplate restTemplate; 

    @GetMapping("/mascotas/listar")
    public ResponseEntity<List<MascotaDTO>> listarMascotas() {
        return ResponseEntity.ok(bffService.obtenerMascotas());
    }

    @PostMapping("/mascotas/reportar")
    public ResponseEntity<MascotaDTO> reportar(
            @RequestBody MascotaDTO mascota,
            @RequestParam(required = false) String tipo) {
        return ResponseEntity.ok(bffService.crearReporte(mascota, tipo));
    }

    @GetMapping("/mascotas/detalle/{id}")
    public ResponseEntity<MascotadetalleDTO> obtenerDetalleMascota(@PathVariable Long id) {
        return ResponseEntity.ok(bffService.obtenerDetalleMascota(id));
    }

    @GetMapping("/dashboard/resumen")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        Map<String, Object> resumen = new HashMap<>();
        String msMascotasUrl = "http://localhost:8081/api/mascotas/count"; 

        Long reportadas = 0L;
        Long activos = 0L;
        Long encontradas = 0L;
        Long pendientes = 0L;

        try { 
            reportadas = restTemplate.getForObject(msMascotasUrl, Long.class); 
        } catch (Exception e) { System.out.println("Error total: " + e.getMessage()); }

        try { 
            activos = restTemplate.getForObject(msMascotasUrl + "?estado={est}", Long.class, "ALERTA: MASCOTA PERDIDA"); 
        } catch (Exception e) { System.out.println("Error activos: " + e.getMessage()); }

        try { 
            encontradas = restTemplate.getForObject(msMascotasUrl + "?estado={est}", Long.class, "EN REFUGIO: MASCOTA ENCONTRADA"); 
        } catch (Exception e) { System.out.println("Error encontradas: " + e.getMessage()); }

        try { 
            pendientes = restTemplate.getForObject(msMascotasUrl + "?estado={est}", Long.class, "REGISTRO NORMAL"); 
        } catch (Exception e) { System.out.println("Error pendientes: " + e.getMessage()); }

        resumen.put("mascotasReportadas", reportadas != null ? reportadas : 0);
        resumen.put("casosActivos", activos != null ? activos : 0);
        resumen.put("mascotasEncontradas", encontradas != null ? encontradas : 0);
        resumen.put("reportesUrgentes", 0L);
        resumen.put("casosCerrados", 0L);
        resumen.put("reportesPendientes", pendientes != null ? pendientes : 0);
        resumen.put("avistamientosRecientes", 6); 

        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/mascotas/ultimos")
    public ResponseEntity<?> getUltimosReportes() {
        try {
            List<?> rawMascotas = restTemplate.getForObject("http://localhost:8081/api/mascotas/listar", List.class);
            return ResponseEntity.ok(rawMascotas);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of()); 
        }
    }

    @PutMapping("/mascotas/actualizar/{id}")
    public ResponseEntity<?> actualizarMascotaAdmin(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            org.springframework.http.HttpEntity<Map<String, Object>> requestEntity = new org.springframework.http.HttpEntity<>(datos);
            return restTemplate.exchange("http://localhost:8081/api/mascotas/" + id, org.springframework.http.HttpMethod.PUT, requestEntity, Void.class);
        } catch (Exception e) {
            System.out.println("❌ Error en túnel PUT del BFF: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF: " + e.getMessage());
        }
    }

    @DeleteMapping("/mascotas/eliminar/{id}")
    public ResponseEntity<?> eliminarMascotaAdmin(@PathVariable Long id) {
        try {
            return restTemplate.exchange("http://localhost:8081/api/mascotas/" + id, org.springframework.http.HttpMethod.DELETE, null, Void.class);
        } catch (Exception e) {
            System.out.println("❌ Error en túnel DELETE del BFF: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/mascotas/crear-admin")
    public ResponseEntity<?> crearMascotaAdmin(@RequestBody Map<String, Object> datos) {
        try {
            System.out.println("📩 [BFF] Procesando insercion directa de mascota admin: " + datos);
            
            ResponseEntity<?> respuesta = restTemplate.postForEntity("http://localhost:8081/api/mascotas/crear-admin", datos, Map.class);
            return ResponseEntity.ok(respuesta.getBody());
        } catch (Exception e) {
            System.out.println("❌ Error en BFF al crear mascota admin: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF: " + e.getMessage());
        }
    }


    @GetMapping("/geo/listar")
    public ResponseEntity<?> listarUbicaciones() {
        try {
            List<?> ubicaciones = restTemplate.getForObject("http://localhost:8082/api/geo/listar", List.class);
            return ResponseEntity.ok(ubicaciones);
        } catch (Exception e) {
            System.out.println("❌ Error pidiendo ubicaciones: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF Geo: " + e.getMessage());
        }
    }

    @GetMapping("/geo/mascota/{id}")
    public ResponseEntity<?> ubicacionesPorMascota(@PathVariable Long id) {
        try {
            List<?> ubicaciones = restTemplate.getForObject("http://localhost:8082/api/geo/mascota/" + id, List.class);
            return ResponseEntity.ok(ubicaciones);
        } catch (Exception e) {
            System.out.println("❌ Error pidiendo historial de mascota: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF Geo: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/geo/registrar")
    public ResponseEntity<?> registrarUbicacion(@RequestBody Map<String, Object> datosGeo) {
        try {
            ResponseEntity<?> respuesta = restTemplate.postForEntity("http://localhost:8082/api/geo/registrar", datosGeo, Map.class);
            return ResponseEntity.ok(respuesta.getBody());
        } catch (Exception e) {
            System.out.println("❌ Error registrando ubicación: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF Geo: " + e.getMessage());
        }
    }
}