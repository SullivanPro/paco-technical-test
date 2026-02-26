package technical.test.api.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.api.facade.FlightFacade;
import technical.test.api.representation.FlightRepresentation;

import java.util.UUID;

@RestController
@RequestMapping("/flight")
@RequiredArgsConstructor
public class FlightEndpoint {
    private final FlightFacade flightFacade;

    @GetMapping
    public Flux<FlightRepresentation> getAllFlights(@RequestParam(required = false) String sort) {
        return flightFacade.getAllFlights(sort);
    }

    @GetMapping("/{id}")
    public Mono<FlightRepresentation> getFlightById(@PathVariable UUID id) {
        return flightFacade.getFlightById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found")));
    }

    @PostMapping
    public Mono<FlightRepresentation> createFlight(@RequestBody FlightRepresentation flightRepresentation) {
        return flightFacade.createFlight(flightRepresentation);
    }

}
