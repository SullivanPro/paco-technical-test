package technical.test.renderer.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.renderer.clients.TechnicalApiClient;
import technical.test.renderer.viewmodels.FlightViewModel;
import technical.test.renderer.viewmodels.FlightCreateRequest;

import java.util.UUID;

@Service
public class FlightService {
    private final TechnicalApiClient technicalApiClient;

    public FlightService(TechnicalApiClient technicalApiClient) {
        this.technicalApiClient = technicalApiClient;
    }

    public Flux<FlightViewModel> getFlights(String sort) {
        return this.technicalApiClient.getFlights(sort);
    }

    public Mono<FlightViewModel> getFlight(UUID id) { return this.technicalApiClient.getFlight(id); }

    public Mono<FlightViewModel> createFlight(FlightCreateRequest request) {
        return this.technicalApiClient.createFlight(request);
    }


}
