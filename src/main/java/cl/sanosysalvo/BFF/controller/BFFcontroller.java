package cl.sanosysalvo.BFF.controller;

import cl.sanosysalvo.BFF.dto.MascotaDTO;
import cl.sanosysalvo.BFF.dto.MascotadetalleDTO;
import cl.sanosysalvo.BFF.service.BFFservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import cl.sanosysalvo.BFF.util.JWTservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "*")
public class BFFcontroller {

    @Autowired
    private BFFservice bffService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JWTservice jwtService;

    @GetMapping("/mascotas/listar")
    public ResponseEntity<List<MascotaDTO>> listarMascotas() {
        return ResponseEntity.ok(bffService.obtenerMascotas());
    }

    @GetMapping("/mascotas/mis-mascotas")
    public ResponseEntity<?> obtenerMisMascotas(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token no enviado o inválido");
        }

        String token = authorizationHeader.replace("Bearer ", "");

        Long usuarioId = jwtService.extractUsuarioId(token);

        System.out.println("📌 [BFF] Cargando mascotas del usuario ID: " + usuarioId);

        List<?> mascotas = restTemplate.getForObject(
                "http://localhost:8081/api/mascotas/usuario/" + usuarioId,
                List.class
        );

        return ResponseEntity.ok(mascotas != null ? mascotas : List.of());

    } catch (Exception e) {
        System.out.println("❌ Error obteniendo mis mascotas: " + e.getMessage());
        return ResponseEntity.status(500).body("Error en BFF al obtener mis mascotas: " + e.getMessage());
    }
}

    @PostMapping("/mascotas/reportar")
    public ResponseEntity<?> reportar(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @RequestBody MascotaDTO mascota,
        @RequestParam(required = false) String tipo) {

        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token no enviado o inválido");
        }

        String token = authorizationHeader.replace("Bearer ", "");

        Long usuarioId = jwtService.extractUsuarioId(token);

        System.out.println("📌 [BFF] Registrando mascota para usuario ID: " + usuarioId);

        mascota.setDueñoId(usuarioId);

        MascotaDTO mascotaCreada = bffService.crearReporte(mascota, tipo);

        return ResponseEntity.ok(mascotaCreada);

    } catch (Exception e) {
        System.out.println("❌ Error reportando mascota con JWT: " + e.getMessage());
        return ResponseEntity.status(500).body("Error en BFF al reportar mascota: " + e.getMessage());
    }
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
        } catch (Exception e) {
            System.out.println("Error total: " + e.getMessage());
        }

        try {
            activos = restTemplate.getForObject(
                    msMascotasUrl + "?estado={est}",
                    Long.class,
                    "ALERTA: MASCOTA PERDIDA"
            );
        } catch (Exception e) {
            System.out.println("Error activos: " + e.getMessage());
        }

        try {
            encontradas = restTemplate.getForObject(
                    msMascotasUrl + "?estado={est}",
                    Long.class,
                    "EN REFUGIO: MASCOTA ENCONTRADA"
            );
        } catch (Exception e) {
            System.out.println("Error encontradas: " + e.getMessage());
        }

        try {
            pendientes = restTemplate.getForObject(
                    msMascotasUrl + "?estado={est}",
                    Long.class,
                    "REGISTRO NORMAL"
            );
        } catch (Exception e) {
            System.out.println("Error pendientes: " + e.getMessage());
        }

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
            List<?> rawMascotas = restTemplate.getForObject(
                    "http://localhost:8081/api/mascotas/listar",
                    List.class
            );
            return ResponseEntity.ok(rawMascotas);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @PutMapping("/mascotas/actualizar/{id}")
    public ResponseEntity<?> actualizarMascotaAdmin(
            @RequestHeader(value = "rol", required = false) String rol,
            @PathVariable Long id,
            @RequestBody Map<String, Object> datos) {

        if (!rolPermitidoParaGestionMascotas(rol)) {
            return ResponseEntity.status(403).body("No tienes permiso para actualizar mascotas");
        }

        try {
            org.springframework.http.HttpEntity<Map<String, Object>> requestEntity =
                    new org.springframework.http.HttpEntity<>(datos);

            return restTemplate.exchange(
                    "http://localhost:8081/api/mascotas/" + id,
                    org.springframework.http.HttpMethod.PUT,
                    requestEntity,
                    Void.class
            );
        } catch (Exception e) {
            System.out.println("❌ Error en túnel PUT del BFF: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF: " + e.getMessage());
        }
    }

    @DeleteMapping("/mascotas/eliminar/{id}")
    public ResponseEntity<?> eliminarMascotaAdmin(
            @RequestHeader(value = "rol", required = false) String rol,
            @PathVariable Long id) {

        if (!rolPermitidoParaGestionMascotas(rol)) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar mascotas");
        }

        try {
            return restTemplate.exchange(
                    "http://localhost:8081/api/mascotas/" + id,
                    org.springframework.http.HttpMethod.DELETE,
                    null,
                    Void.class
            );
        } catch (Exception e) {
            System.out.println("❌ Error en túnel DELETE del BFF: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/mascotas/crear-admin")
    public ResponseEntity<?> crearMascotaAdmin(
            @RequestHeader(value = "rol", required = false) String rol,
            @RequestBody Map<String, Object> datos) {

        if (!rolPermitidoParaGestionMascotas(rol)) {
            return ResponseEntity.status(403).body("No tienes permiso para crear mascotas");
        }

        try {
            System.out.println("📩 [BFF] Rol recibido: " + rol);
            System.out.println("📩 [BFF] Procesando insercion directa de mascota: " + datos);

            ResponseEntity<?> respuesta = restTemplate.postForEntity(
                    "http://localhost:8081/api/mascotas/crear-admin",
                    datos,
                    Map.class
            );

            return ResponseEntity.ok(respuesta.getBody());
        } catch (Exception e) {
            System.out.println("❌ Error en BFF al crear mascota: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF: " + e.getMessage());
        }
    }

    @GetMapping("/adopcion/catalogo")
    public ResponseEntity<?> obtenerCatalogoAdopcion() {
        try {
            List<?> catalogo = restTemplate.getForObject(
                    "http://localhost:8086/api/adopcion/catalogo",
                    List.class
            );

            return ResponseEntity.ok(catalogo);
        } catch (Exception e) {
            System.out.println("❌ Error pidiendo catálogo de adopción: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF Adopción: " + e.getMessage());
        }
    }

    @GetMapping("/adopcion/catalogo/detalle/{id}")
    public ResponseEntity<?> obtenerDetalleAdopcion(@PathVariable Long id) {
        try {
            Object detalle = restTemplate.getForObject(
                    "http://localhost:8086/api/adopcion/catalogo/" + id,
                    Object.class
            );

            return ResponseEntity.ok(detalle);
        } catch (Exception e) {
            System.out.println("❌ Error pidiendo detalle de adopción: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF Adopción: " + e.getMessage());
        }
    }

    @GetMapping("/adopcion/catalogo/especie/{especie}")
    public ResponseEntity<?> obtenerCatalogoAdopcionPorEspecie(@PathVariable String especie) {
        try {
            List<?> catalogo = restTemplate.getForObject(
                    "http://localhost:8086/api/adopcion/catalogo/especie/" + especie,
                    List.class
            );

            return ResponseEntity.ok(catalogo);
        } catch (Exception e) {
            System.out.println("❌ Error filtrando adopción por especie: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF Adopción: " + e.getMessage());
        }
    }

    @GetMapping("/adopcion/catalogo/ubicacion/{ubicacion}")
    public ResponseEntity<?> obtenerCatalogoAdopcionPorUbicacion(@PathVariable String ubicacion) {
        try {
            List<?> catalogo = restTemplate.getForObject(
                    "http://localhost:8086/api/adopcion/catalogo/ubicacion/" + ubicacion,
                    List.class
            );

            return ResponseEntity.ok(catalogo);
        } catch (Exception e) {
            System.out.println("❌ Error filtrando adopción por ubicación: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF Adopción: " + e.getMessage());
        }
    }

    @GetMapping("/geo/listar")
    public ResponseEntity<?> listarUbicaciones() {
        try {
            List<?> ubicaciones = restTemplate.getForObject(
                    "http://localhost:8084/api/geo/listar",
                    List.class
            );
            return ResponseEntity.ok(ubicaciones);
        } catch (Exception e) {
            System.out.println("❌ Error pidiendo ubicaciones: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF Geo: " + e.getMessage());
        }
    }

    @GetMapping("/geo/mascota/{id}")
    public ResponseEntity<?> ubicacionesPorMascota(@PathVariable Long id) {
        try {
            List<?> ubicaciones = restTemplate.getForObject(
                    "http://localhost:8084/api/geo/mascota/" + id,
                    List.class
            );
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
            ResponseEntity<?> respuesta = restTemplate.postForEntity(
                    "http://localhost:8084/api/geo/registrar",
                    datosGeo,
                    Map.class
            );
            return ResponseEntity.ok(respuesta.getBody());
        } catch (Exception e) {
            System.out.println("❌ Error registrando ubicación: " + e.getMessage());
            return ResponseEntity.status(500).body("Error en BFF Geo: " + e.getMessage());
        }
    }

    private boolean rolPermitidoParaGestionMascotas(String rol) {
        return rol != null &&
                (rol.equalsIgnoreCase("ADMIN") || rol.equalsIgnoreCase("VETERINARIO"));
    }
}