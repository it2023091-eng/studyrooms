package gr.hua.dit.studyrooms.core.repository;




import gr.hua.dit.studyrooms.core.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}


