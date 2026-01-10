package gr.hua.dit.studyrooms.adapters.holidays;



import gr.hua.dit.studyrooms.core.port.HolidayPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;

@Component
public class HolidayHttpAdapter implements HolidayPort {

    private static final String BASE_URL = "https://date.nager.at/api/v3/PublicHolidays";

    private final RestTemplate restTemplate;

    public HolidayHttpAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        String url = BASE_URL + "/" + date.getYear() + "/GR";

        try {
            HolidayDto[] holidays =
                    restTemplate.getForObject(url, HolidayDto[].class);

            if (holidays == null) return false;

            return Arrays.stream(holidays)
                    .anyMatch(h -> h.date.equals(date.toString()));

        } catch (RestClientException ex) {
            // Αν πέσει το external API, ΔΕΝ μπλοκάρουμε το booking
            return false;
        }
    }

    // DTO σύμφωνα με το public API
    static class HolidayDto {
        public String date;       // yyyy-MM-dd
        public String localName;
        public String name;
    }
}
