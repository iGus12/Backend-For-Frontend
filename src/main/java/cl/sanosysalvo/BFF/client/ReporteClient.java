package cl.sanosysalvo.BFF.client;

import cl.sanosysalvo.BFF.dto.ReporteDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ReporteClient {

    private final RestTemplate restTemplate;

    public ReporteClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ReporteDTO> obtenerReportesPorMascota(Long idMascota) {
        String url = "http://localhost:8082/api/reportes/mascota/" + idMascota;

        ResponseEntity<List<ReporteDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ReporteDTO>>() {}
        );

        return response.getBody();
    }
}