package gr.hua.dit.studyrooms.web.ui;


import gr.hua.dit.studyrooms.core.service.BookingService;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class RoomsUiController {

    private final StudyRoomService studyRoomService;
    private final BookingService bookingService;

    public RoomsUiController(StudyRoomService studyRoomService, BookingService bookingService) {
        this.studyRoomService = studyRoomService;
        this.bookingService = bookingService;
    }

    @GetMapping("/rooms")
    public String showRooms(Model model) {
        var rooms = studyRoomService.getAllRooms();
        model.addAttribute("rooms", rooms);

        Map<Long, String> nextFreeLabel = new LinkedHashMap<>();

        for (var r : rooms) {
            var next = bookingService.getNextSlotAvailability(r.getId());
            if (next == null) {
                nextFreeLabel.put(r.getId(), "-");
            } else {
                nextFreeLabel.put(
                        r.getId(),
                        next.getStart().toLocalTime() + " → " + next.getFreeSeats() + "/" + r.getCapacity()
                );
            }
        }

        model.addAttribute("nextFreeLabel", nextFreeLabel);
        return "rooms";
    }
}
