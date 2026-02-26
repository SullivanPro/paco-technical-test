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

import java.util.Comparator;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FlightFacade {
    private final FlightService flightService;
    private final AirportService airportService;
    private final FlightMapper flightMapper;
    private final AirportMapper airportMapper;

    public Flux<FlightRepresentation> getAllFlights(String sort) {
        return flightService.getAllFlights()
                .flatMap(flightRecord ->
                        airportService.findByIataCode(flightRecord.getOrigin())
                                .zipWith(airportService.findByIataCode(flightRecord.getDestination()))
                                .map(tuple -> {
                                    var origin = tuple.getT1();
                                    var destination = tuple.getT2();
                                    var flightRepresentation = this.flightMapper.convert(flightRecord);
                                    flightRepresentation.setOrigin(this.airportMapper.convert(origin));
                                    flightRepresentation.setDestination(this.airportMapper.convert(destination));
                                    return flightRepresentation;
                                })
                )
                // Optional sorting
                .transform(flux -> applySort(flux, sort));
    }

    /**
     * Recovers flights and applies sorting
     *
     * @param sort sorting criteria (price, -price, destination, -destination)
     * @return sorted flights
     */
    private Flux<FlightRepresentation> applySort(Flux<FlightRepresentation> flux, String sort) {
        if (sort == null || sort.isBlank()) return flux;

        return switch (sort) {
            case "price"      -> flux.sort(Comparator.comparing(FlightRepresentation::getPrice));
            case "-price"     -> flux.sort(Comparator.comparing(FlightRepresentation::getPrice).reversed());
            case "destination"   -> flux.sort(Comparator.comparing(rep -> rep.getDestination().getIata()));
            case "-destination"  -> flux.sort(Comparator.comparing((FlightRepresentation rep) -> rep.getDestination().getIata()).reversed());
            default -> flux;
        };
    }


    /**
     * Retrieves a single flight by its unique identifier.
     *
     * @param id the UUID of the flight to retrieve
     * @return the flight representation or a 404 error
     */
     public Mono<FlightRepresentation> getFlightById(UUID id) {
        return flightService.getFlightById(id)
                .flatMap(flightRecord ->
                        airportService.findByIataCode(flightRecord.getOrigin())
                                .zipWith(airportService.findByIataCode(flightRecord.getDestination()))
                                .map(tuple -> {
                                    var origin = tuple.getT1();
                                    var destination = tuple.getT2();
                                    var rep = this.flightMapper.convert(flightRecord);
                                    rep.setOrigin(this.airportMapper.convert(origin));
                                    rep.setDestination(this.airportMapper.convert(destination));
                                    return rep;
                                })
                );
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
