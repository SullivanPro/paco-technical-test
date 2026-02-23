package technical.test.api.record;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "airport")
public class AirportRecord {
    @Id
    private String iata;
    private String name;
    private String country;
}
