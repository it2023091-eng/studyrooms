package gr.hua.dit.studyrooms.core.repository;



import gr.hua.dit.studyrooms.core.model.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {
}
