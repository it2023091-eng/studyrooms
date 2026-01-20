package gr.hua.dit.studyrooms.web.ui;



import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff/rooms")
public class StaffRoomsUiController {

    private final StudyRoomService studyRoomService;

    public StaffRoomsUiController(StudyRoomService studyRoomService) {
        this.studyRoomService = studyRoomService;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("rooms", studyRoomService.getAllRooms());
        return "staff-rooms";
    }

    @PostMapping("/create")
    public String create(@RequestParam String name,
                         @RequestParam int capacity) {

        StudyRoom room = new StudyRoom();
        room.setName(name);
        room.setCapacity(capacity);
        room.setAvailable(true); // default διαθέσιμο

        studyRoomService.createRoom(room);
        return "redirect:/staff/rooms?created";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        StudyRoom room = studyRoomService.getRoomById(id);
        if (room != null) {
            room.setAvailable(!room.isAvailable());
            studyRoomService.createRoom(room); // save update
            return "redirect:/staff/rooms?toggled";
        }
        return "redirect:/staff/rooms?notfound";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        studyRoomService.deleteRoom(id);
        return "redirect:/staff/rooms?deleted";
    }
}
