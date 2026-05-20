package cl.sanosysalvo.BFF.client;

import cl.sanosysalvo.BFF.dto.MascotaDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Component
public class MascotaCliente { 

    private final RestTemplate restTemplate;
    
    private final String URL_BASE = "http://localhost:8081/api/mascotas"; 

    public MascotaCliente(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 1. El que ya tenías para el detalle
    public MascotaDTO obtenerMascotaPorId(Long id) {
        return restTemplate.getForObject(URL_BASE + "/" + id, MascotaDTO.class);
    }

    // 2. NUEVO: Para el listado del controlador
    public List<MascotaDTO> listarTodas() {
        MascotaDTO[] respuesta = restTemplate.getForObject(URL_BASE + "/listar", MascotaDTO[].class);
        return Arrays.asList(respuesta);
    }

    // 3. NUEVO: Para crear el reporte
    public MascotaDTO enviarReporte(MascotaDTO mascota, String tipo) {
        String url = (tipo != null) ? URL_BASE + "/reportar?tipo=" + tipo : URL_BASE + "/reportar";
        return restTemplate.postForObject(url, mascota, MascotaDTO.class);
    }
}