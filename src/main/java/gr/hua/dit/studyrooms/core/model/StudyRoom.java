package gr.hua.dit.studyrooms.core.model;


import jakarta.persistence.*;
        import java.util.Objects;

@Entity
@Table(name = "study_room")
public class StudyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int capacity;
    private boolean available;

    public StudyRoom() {
    }

    public StudyRoom(String name, int capacity, boolean available) {
        this.name = name;
        this.capacity = capacity;
        this.available = available;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudyRoom)) return false;
        StudyRoom that = (StudyRoom) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
