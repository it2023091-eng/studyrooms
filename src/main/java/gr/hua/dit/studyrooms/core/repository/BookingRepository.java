package gr.hua.dit.studyrooms.core.repository;

import gr.hua.dit.studyrooms.core.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserEmailOrderByStartTimeDesc(String email);

    @Query("""
        select count(b) from Booking b
        where b.user.email = :email
          and b.startTime >= :from
          and b.startTime < :to
    """)
    long countForUserInRange(String email, LocalDateTime from, LocalDateTime to);

    @Query("""
        select count(b) from Booking b
        where b.studyRoom.id = :roomId
          and b.startTime < :end
          and b.endTime > :start
    """)
    long countOverlaps(Long roomId, LocalDateTime start, LocalDateTime end);

    @Query("""
    select coalesce(sum(coalesce(b.seats, 1)), 0)
    from Booking b
    where b.studyRoom.id = :roomId
      and b.startTime < :end
      and b.endTime > :start
""")
    int sumBookedSeats(Long roomId, LocalDateTime start, LocalDateTime end);



}


