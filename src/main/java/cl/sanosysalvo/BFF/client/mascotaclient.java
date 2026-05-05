package cl.sanosysalvo.BFF.client;

import cl.sanosysalvo.BFF.dto.MascotaDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class mascotaclient {

    private final RestTemplate restTemplate;

    public mascotaclient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MascotaDTO obtenerMascotaPorId(Long id) {
        String url = "http://localhost:8081/api/mascotas/" + id;
        return restTemplate.getForObject(url, MascotaDTO.class);
    }
}