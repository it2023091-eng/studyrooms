package gr.hua.dit.studyrooms.web.rest.model;


import java.time.LocalDateTime;

public class BookingCreateRequest {
    private String studentName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long studyRoomId;

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Long getStudyRoomId() { return studyRoomId; }
    public void setStudyRoomId(Long studyRoomId) { this.studyRoomId = studyRoomId; }
}
