package gr.hua.dit.studyrooms.core.service;




import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudyRoomServiceImpl implements StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;

    public StudyRoomServiceImpl(StudyRoomRepository studyRoomRepository) {
        this.studyRoomRepository = studyRoomRepository;
    }

    @Override
    public List<StudyRoom> getAllRooms() {
        return studyRoomRepository.findAll();
    }

    @Override
    public StudyRoom getRoomById(Long id) {
        Optional<StudyRoom> room = studyRoomRepository.findById(id);
        return room.orElse(null);
    }

    @Override
    public StudyRoom createRoom(StudyRoom room) {
        return studyRoomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        studyRoomRepository.deleteById(id);
    }
}
