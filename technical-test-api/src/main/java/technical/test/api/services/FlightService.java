package technical.test.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.api.record.FlightRecord;
import technical.test.api.repository.FlightRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;

    public Flux<FlightRecord> getAllFlights() {
        return flightRepository.findAll();
    }

    public Mono<FlightRecord> getFlightById(UUID id) { return flightRepository.findById(id); }

    public Mono<FlightRecord> createFlight(FlightRecord flightRecord) {
        return flightRepository.save(flightRecord);
    }

}
