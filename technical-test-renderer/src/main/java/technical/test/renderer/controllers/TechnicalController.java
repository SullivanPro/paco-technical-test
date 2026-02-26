package technical.test.renderer.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import technical.test.renderer.facades.FlightFacade;
import technical.test.renderer.viewmodels.FlightCreateRequest;

import java.util.UUID;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class TechnicalController {

    @Autowired
    private FlightFacade flightFacade;

    @GetMapping
    public Mono<String> getMarketPlaceReturnCouponPage(@RequestParam(required = false) String sort, final Model model) {
        return this.flightFacade.getFlights(sort)
                .collectList() // To respect the backend sorting
                .doOnNext(flights -> {
                    model.addAttribute("flights", flights);
                    model.addAttribute("sort", sort);
                })
                .thenReturn("pages/index");
    }


    @GetMapping("/flights/{id}")
    public Mono<String> showFlightDetails(@PathVariable UUID id, Model model) {
        return flightFacade.getFlight(id)
                .doOnNext(f -> model.addAttribute("flight", f))
                .switchIfEmpty(Mono.fromRunnable(() -> model.addAttribute("flight", null)))
                .thenReturn("pages/flight/details");
    }


    @GetMapping("/admin")
    public String showAdminPage(Model model) {
        model.addAttribute("flight", new FlightCreateRequest());
        return "pages/admin/new-flight";
    }

    @PostMapping("/admin")
    public Mono<String> createFlight(@ModelAttribute FlightCreateRequest flight) {
        return flightFacade.createFlight(flight)
                .thenReturn("redirect:/");
    }

}
