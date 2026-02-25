package technical.test.renderer.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.renderer.properties.TechnicalApiProperties;
import technical.test.renderer.viewmodels.FlightCreateRequest;
import technical.test.renderer.viewmodels.FlightViewModel;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@Slf4j
public class TechnicalApiClient {

    private final TechnicalApiProperties technicalApiProperties;
    private final WebClient webClient;

    public TechnicalApiClient(TechnicalApiProperties technicalApiProperties, final WebClient.Builder webClientBuilder) {
        this.technicalApiProperties = technicalApiProperties;
        this.webClient = webClientBuilder.build();
    }

    public Flux<FlightViewModel> getFlights(String sort) {
        String base = technicalApiProperties.getUrl();
        String path = technicalApiProperties.getFlightPath();

        String url = base + path;
        if (sort != null && !sort.isBlank()) {
            url += "?sort=" + URLEncoder.encode(sort, StandardCharsets.UTF_8);
        }

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(FlightViewModel.class);
    }

    public Mono<FlightViewModel> createFlight(FlightCreateRequest request) {
        return webClient
                .post()
                .uri(technicalApiProperties.getUrl() + technicalApiProperties.getFlightPath())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FlightViewModel.class);
    }

}
