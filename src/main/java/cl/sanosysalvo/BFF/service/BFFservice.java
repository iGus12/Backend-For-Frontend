package cl.sanosysalvo.BFF.service;

import cl.sanosysalvo.BFF.client.mascotaclient;
import cl.sanosysalvo.BFF.client.ReporteClient;
import cl.sanosysalvo.BFF.dto.MascotaDTO;
import cl.sanosysalvo.BFF.dto.MascotadetalleDTO;
import cl.sanosysalvo.BFF.dto.ReporteDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BFFservice {

    private final mascotaclient mascotaClient;
    private final ReporteClient reporteClient;

    public BFFservice(mascotaclient mascotaClient, ReporteClient reporteClient) {
        this.mascotaClient = mascotaClient;
        this.reporteClient = reporteClient;
    }

    public MascotadetalleDTO obtenerDetalleMascota(Long id) {
        MascotaDTO mascota = mascotaClient.obtenerMascotaPorId(id);

        List<ReporteDTO> reportes;
        try {
            reportes = reporteClient.obtenerReportesPorMascota(id);
        } catch (Exception e) {
            reportes = new ArrayList<>();
        }

        MascotadetalleDTO detalle = new MascotadetalleDTO();
        detalle.setNombre(mascota.getNombre());
        detalle.setEspecie(mascota.getEspecie());
        detalle.setRaza(mascota.getRaza());
        detalle.setEdad(mascota.getEdad());
        detalle.setDueñoId(mascota.getDueñoId());
        detalle.setVacunas(mascota.getVacunas());
        detalle.setReportes(reportes);

        return detalle;
    }
}