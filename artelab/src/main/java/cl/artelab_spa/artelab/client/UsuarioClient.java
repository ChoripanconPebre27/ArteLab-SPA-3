package cl.artelab_spa.artelab.client;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import cl.artelab_spa.artelab.dto.UsuarioLookupDto;
import cl.artelab_spa.artelab.exception.RemoteServiceException;
import reactor.core.publisher.Mono;

@Service
public class UsuarioClient {

    private static final Logger log = LoggerFactory.getLogger(UsuarioClient.class);

    private final WebClient usuariosWebClient;

    public UsuarioClient(WebClient usuariosWebClient) {
        this.usuariosWebClient = usuariosWebClient;
    }

    public Optional<UsuarioLookupDto> findUsuarioById(Long id) {
        try {
            return usuariosWebClient.get()
                    .uri("/api/v1/usuarios/{id}/lookup", id)
                    .exchangeToMono(this::handleResponse)
                    .block();
        } catch (RemoteServiceException ex) {
            log.error("Remote usuarios service failure for id={}", id, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error calling usuarios service for id={}", id, ex);
            throw new RemoteServiceException("Error connecting to usuarios service", ex);
        }
    }

    private Mono<Optional<UsuarioLookupDto>> handleResponse(ClientResponse response) {
        if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
            return Mono.just(Optional.empty());
        }
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(UsuarioLookupDto.class).map(Optional::ofNullable);
        }
        return response.bodyToMono(String.class)
                .defaultIfEmpty(response.statusCode().toString())
                .flatMap(body -> Mono.error(new RemoteServiceException(
                        "Usuarios service returned " + response.statusCode() + ": " + body)));
    }
}
