package technical.test.renderer.viewmodels;

import lombok.Data;

@Data
public class FlightCreateRequest {
    private AirportRef origin = new AirportRef();
    private AirportRef destination = new AirportRef();
    private Double price;
    private String image;

    @Data
    public static class AirportRef {
        private String iata;
    }
}
