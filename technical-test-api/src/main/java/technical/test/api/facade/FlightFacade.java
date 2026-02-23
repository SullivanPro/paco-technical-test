package technical.test.api.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.api.mapper.AirportMapper;
import technical.test.api.mapper.FlightMapper;
import technical.test.api.record.AirportRecord;
import technical.test.api.representation.FlightRepresentation;
import technical.test.api.services.AirportService;
import technical.test.api.services.FlightService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FlightFacade {
    private final FlightService flightService;
    private final AirportService airportService;
    private final FlightMapper flightMapper;
    private final AirportMapper airportMapper;

    public Flux<FlightRepresentation> getAllFlights() {
        return flightService.getAllFlights()
                .flatMap(flightRecord -> airportService.findByIataCode(flightRecord.getOrigin())
                        .zipWith(airportService.findByIataCode(flightRecord.getDestination()))
                        .flatMap(tuple -> {
                            AirportRecord origin = tuple.getT1();
                            AirportRecord destination = tuple.getT2();
                            FlightRepresentation flightRepresentation = this.flightMapper.convert(flightRecord);
                            flightRepresentation.setOrigin(this.airportMapper.convert(origin));
                            flightRepresentation.setDestination(this.airportMapper.convert(destination));
                            return Mono.just(flightRepresentation);
                        }));
    }


    /**
     * Create a flight from the representation provided
     * then returns the record flight
     *
     * @param flightRepresentation flight data to be created
     * @return flight created with this information
    **/
    public Mono<FlightRepresentation> createFlight(FlightRepresentation flightRepresentation) {

        return airportService.findByIataCode(flightRepresentation.getOrigin().getIata())
                .zipWith(airportService.findByIataCode(flightRepresentation.getDestination().getIata()))
                .flatMap(tuple -> {

                    // Convert incoming representation → record
                    var flightRecord = flightMapper.convert(flightRepresentation);

                    if (flightRecord.getId() == null) {
                        flightRecord.setId(UUID.randomUUID());
                    }

                    return flightService.createFlight(flightRecord)
                            .map(savedRecord -> {
                                FlightRepresentation response = flightMapper.convert(savedRecord);
                                response.setOrigin(airportMapper.convert(tuple.getT1()));
                                response.setDestination(airportMapper.convert(tuple.getT2()));
                                return response;
                            });
                });
    }

}
