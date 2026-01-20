package gr.hua.dit.studyrooms.web.ui;



import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.service.BookingService;
import gr.hua.dit.studyrooms.core.service.dto.SlotAvailability;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import gr.hua.dit.studyrooms.web.rest.model.BookingCreateRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class BookingUiController {

    private final StudyRoomService studyRoomService;
    private final BookingService bookingService;

    public BookingUiController(StudyRoomService studyRoomService, BookingService bookingService) {
        this.studyRoomService = studyRoomService;
        this.bookingService = bookingService;
    }

    private boolean isStaff(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));
    }

    // ✅ Booking page (STUDENT only)
    @GetMapping("/rooms/{id}/book")
    public String bookForm(@PathVariable Long id, Model model, Authentication auth) {

        // Staff must not book
        if (isStaff(auth)) {
            return "redirect:/rooms?forbidden";
        }

        StudyRoom room = studyRoomService.getRoomById(id);
        model.addAttribute("room", room);

        // ✅ calendar: Map<LocalDate, List<SlotAvailability>>
        Map<LocalDate, List<SlotAvailability>> calendar = bookingService.getAvailabilityCalendar(id);
        model.addAttribute("calendar", calendar);

        return "book-room";
    }

    // ✅ Create booking from selected slot (STUDENT only)
    @PostMapping("/rooms/{id}/book")
    public String createBooking(@PathVariable Long id,
                                @RequestParam String start,
                                Authentication auth,
                                Model model) {

        // Staff must not book
        if (isStaff(auth)) {
            return "redirect:/rooms?forbidden";
        }

        try {
            // start comes from the radio input; end is automatically +1 hour
            var startDt = java.time.LocalDateTime.parse(start);
            var endDt = startDt.plusHours(1);

            BookingCreateRequest req = new BookingCreateRequest();
            req.setStudyRoomId(id);
            req.setStartTime(startDt);
            req.setEndTime(endDt);
            req.setStudentName(auth.getName());

            bookingService.createBooking(req);
            return "redirect:/my-bookings?success";

        } catch (Exception ex) {
            StudyRoom room = studyRoomService.getRoomById(id);
            model.addAttribute("room", room);

            Map<LocalDate, List<SlotAvailability>> calendar = bookingService.getAvailabilityCalendar(id);
            model.addAttribute("calendar", calendar);

            model.addAttribute("error", ex.getMessage());
            return "book-room";
        }
    }

    // ✅ My bookings (STUDENT only)
    @GetMapping("/my-bookings")
    public String myBookings(Model model, Authentication auth) {

        if (isStaff(auth)) {
            return "redirect:/rooms?forbidden";
        }

        model.addAttribute("bookings", bookingService.getMyBookings());
        return "my-bookings";
    }

    // ✅ Cancel booking (STUDENT only + ownership check)
    @PostMapping("/bookings/{id}/cancel")
    public String cancel(@PathVariable Long id, Authentication auth) {

        if (isStaff(auth)) {
            return "redirect:/rooms?forbidden";
        }

        Booking b = bookingService.getBookingById(id);
        if (b != null && b.getUser() != null && b.getUser().getEmail().equals(auth.getName())) {
            bookingService.deleteBooking(id);
            return "redirect:/my-bookings?canceled";
        }

        return "redirect:/my-bookings?forbidden";
    }
}
