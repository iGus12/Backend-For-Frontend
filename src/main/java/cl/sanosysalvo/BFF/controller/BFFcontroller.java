package cl.sanosysalvo.BFF.controller;

import cl.sanosysalvo.BFF.dto.MascotaDTO;
import cl.sanosysalvo.BFF.dto.MascotadetalleDTO; 
import cl.sanosysalvo.BFF.service.BFFservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "http://localhost:5173") 
public class BFFcontroller {

    @Autowired
    private BFFservice bffService;

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
}