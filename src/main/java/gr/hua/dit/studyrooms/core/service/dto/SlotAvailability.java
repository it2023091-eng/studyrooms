package gr.hua.dit.studyrooms.core.service.dto;


import java.time.LocalDateTime;

public class SlotAvailability {
    private final LocalDateTime start;
    private final int freeSeats;

    public SlotAvailability(LocalDateTime start, int freeSeats) {
        this.start = start;
        this.freeSeats = freeSeats;
    }

    public LocalDateTime getStart() { return start; }
    public int getFreeSeats() { return freeSeats; }
}

