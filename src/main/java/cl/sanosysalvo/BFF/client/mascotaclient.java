package cl.sanosysalvo.BFF.client;

import cl.sanosysalvo.BFF.dto.MascotaDTO;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class mascotaclient {

    private final RestTemplate restTemplate;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public mascotaclient(RestTemplate restTemplate, CircuitBreakerFactory circuitBreakerFactory) {
        this.restTemplate = restTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public MascotaDTO obtenerMascotaPorId(Long id) {
        String url = "http://localhost:8081/api/mascotas/" + id;

        return (MascotaDTO) circuitBreakerFactory
                .create("mascotasService")
                .run(
                        () -> restTemplate.getForObject(url, MascotaDTO.class),
                        throwable -> fallbackObtenerMascotaPorId(id, throwable)
                );
    }

    public MascotaDTO fallbackObtenerMascotaPorId(Long id, Throwable throwable) {
        System.out.println("Servicio de mascotas no disponible. ID solicitado: " + id);
        return new MascotaDTO();
    }
}