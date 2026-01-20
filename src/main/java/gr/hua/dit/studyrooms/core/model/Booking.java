package gr.hua.dit.studyrooms.core.model;




import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    @Column(nullable = false)
    private Integer seats = 1;

    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }



    public StudyRoom getStudyRoom() { return studyRoom; }
    public void setStudyRoom(StudyRoom studyRoom) { this.studyRoom = studyRoom; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

