package technical.test.renderer.facades;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.renderer.services.FlightService;
import technical.test.renderer.viewmodels.FlightCreateRequest;
import technical.test.renderer.viewmodels.FlightViewModel;

import java.util.UUID;

@Component
public class FlightFacade {

    private final FlightService flightService;

    public FlightFacade(FlightService flightService) {
        this.flightService = flightService;
    }

    public Flux<FlightViewModel> getFlights(String sort) {
        return this.flightService.getFlights(sort);
    }

    public Mono<FlightViewModel> getFlight(UUID id) { return this.flightService.getFlight(id); }

    public Mono<FlightViewModel> createFlight(FlightCreateRequest request) {
        return this.flightService.createFlight(request);
    }

}
