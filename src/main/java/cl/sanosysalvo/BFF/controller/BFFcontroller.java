package cl.sanosysalvo.BFF.controller;

import cl.sanosysalvo.BFF.dto.MascotadetalleDTO;
import cl.sanosysalvo.BFF.service.BFFservice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff")
@CrossOrigin("*")
public class BFFcontroller {

    private final BFFservice bffService;

    public BFFcontroller(BFFservice bffService) {
        this.bffService = bffService;
    }

    @GetMapping("/mascotas/{id}/detalle")
    public MascotadetalleDTO obtenerDetalleMascota(@PathVariable Long id) {
        return bffService.obtenerDetalleMascota(id);
    }
}