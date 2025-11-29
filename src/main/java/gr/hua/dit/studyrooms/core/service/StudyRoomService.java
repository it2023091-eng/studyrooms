package gr.hua.dit.studyrooms.core.service;


import gr.hua.dit.studyrooms.core.model.StudyRoom;
import java.util.List;

public interface StudyRoomService {
    List<StudyRoom> getAllRooms();
    StudyRoom getRoomById(Long id);
    StudyRoom createRoom(StudyRoom room);
    void deleteRoom(Long id);
}
