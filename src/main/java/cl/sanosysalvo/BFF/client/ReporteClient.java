package cl.sanosysalvo.BFF.client;

import cl.sanosysalvo.BFF.dto.ReporteDTO;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class ReporteClient {

    private final RestTemplate restTemplate;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public ReporteClient(RestTemplate restTemplate, CircuitBreakerFactory circuitBreakerFactory) {
        this.restTemplate = restTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public List<ReporteDTO> obtenerReportesPorMascota(Long idMascota) {
        String url = "http://localhost:8081/api/reportes/mascota/" + idMascota;

        return (List<ReporteDTO>) circuitBreakerFactory
                .create("reportesService")
                .run(
                        () -> {
                            ResponseEntity<List<ReporteDTO>> response = restTemplate.exchange(
                                    url,
                                    HttpMethod.GET,
                                    null,
                                    new ParameterizedTypeReference<List<ReporteDTO>>() {}
                            );

                            return response.getBody();
                        },
                        throwable -> fallbackObtenerReportesPorMascota(idMascota, throwable)
                );
    }

    public List<ReporteDTO> fallbackObtenerReportesPorMascota(Long idMascota, Throwable throwable) {
        System.out.println("Servicio de reportes no disponible. Mascota solicitada: " + idMascota);
        return Collections.emptyList();
    }
}