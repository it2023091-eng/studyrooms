package gr.hua.dit.studyrooms.web.ui;

import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class RoomsUiController {

    private final StudyRoomService studyRoomService;

    public RoomsUiController(StudyRoomService studyRoomService) {
        this.studyRoomService = studyRoomService;
    }

    // /rooms -> εμφανίζει λίστα με τα δωμάτια
    @GetMapping("/rooms")
    public String showRooms(Model model) {
        model.addAttribute("rooms", studyRoomService.getAllRooms());
        return "rooms";   // θα ψάξει το rooms.html στα templates
    }
}
