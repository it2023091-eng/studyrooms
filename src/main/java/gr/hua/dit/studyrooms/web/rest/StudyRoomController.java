package gr.hua.dit.studyrooms.web.rest;

import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    public StudyRoomController(StudyRoomService studyRoomService) {
        this.studyRoomService = studyRoomService;
    }

    @GetMapping
    public ResponseEntity<List<StudyRoom>> getAllRooms() {
        return ResponseEntity.ok(studyRoomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyRoom> getRoomById(@PathVariable Long id) {
        StudyRoom room = studyRoomService.getRoomById(id);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    @PostMapping
    public ResponseEntity<StudyRoom> createRoom(@RequestBody StudyRoom room) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studyRoomService.createRoom(room));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        studyRoomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}

