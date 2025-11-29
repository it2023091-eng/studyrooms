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

    @ManyToOne
    @JoinColumn(name = "study_room_id")
    private StudyRoom studyRoom;

    public Booking() {
    }

    public Booking(String studentName, LocalDateTime startTime, LocalDateTime endTime, StudyRoom studyRoom) {
        this.studentName = studentName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.studyRoom = studyRoom;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public StudyRoom getStudyRoom() { return studyRoom; }
    public void setStudyRoom(StudyRoom studyRoom) { this.studyRoom = studyRoom; }
}

