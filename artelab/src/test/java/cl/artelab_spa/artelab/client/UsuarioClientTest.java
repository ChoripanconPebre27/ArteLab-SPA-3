package cl.artelab_spa.artelab.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import cl.artelab_spa.artelab.dto.UsuarioLookupDto;
import cl.artelab_spa.artelab.exception.RemoteServiceException;
import reactor.core.publisher.Mono;

class UsuarioClientTest {

    @Test
    void findUsuarioByIdReturnsDtoWhenRemoteServiceRespondsOk() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(request -> {
                    assertEquals(HttpMethod.GET, request.method());
                    assertTrue(request.url().toString().endsWith("/api/v1/usuarios/7/lookup"));
                    return Mono.just(ClientResponse.create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body("{\"id\":7,\"nombreUsuario\":\"ana\",\"correo\":\"ana@mail.cl\"}")
                            .build());
                })
                .build();
        UsuarioClient client = new UsuarioClient(webClient);

        Optional<UsuarioLookupDto> result = client.findUsuarioById(7L);

        assertTrue(result.isPresent());
        assertEquals("ana", result.get().getNombreUsuario());
    }

    @Test
    void findUsuarioByIdReturnsEmptyWhenRemoteServiceRespondsNotFound() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(request -> Mono.just(ClientResponse.create(HttpStatus.NOT_FOUND).build()))
                .build();
        UsuarioClient client = new UsuarioClient(webClient);

        Optional<UsuarioLookupDto> result = client.findUsuarioById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findUsuarioByIdThrowsRemoteServiceExceptionOnServerError() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(request -> Mono.just(ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                        .body("boom")
                        .build()))
                .build();
        UsuarioClient client = new UsuarioClient(webClient);

        assertThrows(RemoteServiceException.class, () -> client.findUsuarioById(7L));
    }

    @Test
    void findUsuarioByIdWrapsConnectionFailures() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(request -> Mono.error(new IllegalStateException("down")))
                .build();
        UsuarioClient client = new UsuarioClient(webClient);

        assertThrows(RemoteServiceException.class, () -> client.findUsuarioById(7L));
    }
}
