package gr.hua.dit.studyrooms.web.ui.dto;


import java.time.LocalDateTime;

public class SlotView {
    private final LocalDateTime start;
    private final String label;

    public SlotView(LocalDateTime start, String label) {
        this.start = start;
        this.label = label;
    }

    public LocalDateTime getStart() { return start; }
    public String getLabel() { return label; }
}
