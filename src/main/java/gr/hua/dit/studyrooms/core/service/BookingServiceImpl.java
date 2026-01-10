package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.model.User;
import gr.hua.dit.studyrooms.core.port.HolidayPort;
import gr.hua.dit.studyrooms.core.repository.BookingRepository;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.repository.UserRepository;
import gr.hua.dit.studyrooms.web.rest.model.BookingCreateRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private static final int MAX_BOOKINGS_PER_DAY = 2;

    private final BookingRepository bookingRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final UserRepository userRepository;

    private final HolidayPort holidayPort;

    // ✅ ΕΝΑΣ constructor με ΟΛΑ τα dependencies
    public BookingServiceImpl(BookingRepository bookingRepository,
                              StudyRoomRepository studyRoomRepository,
                              UserRepository userRepository,
                              HolidayPort holidayPort) {
        this.bookingRepository = bookingRepository;
        this.studyRoomRepository = studyRoomRepository;
        this.userRepository = userRepository;
        this.holidayPort = holidayPort;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getMyBookings() {
        String email = currentUserEmail();
        return bookingRepository.findByUserEmailOrderByStartTimeDesc(email);
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    @Override
    public Booking createBooking(BookingCreateRequest req) {

        // 1) Basic validation
        if (req.getStudyRoomId() == null) {
            throw new IllegalArgumentException("studyRoomId is required");
        }
        if (req.getStartTime() == null || req.getEndTime() == null) {
            throw new IllegalArgumentException("startTime and endTime are required");
        }
        if (!req.getEndTime().isAfter(req.getStartTime())) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }

        // ✅ External service rule: no bookings on public holidays
        LocalDate bookingDay = req.getStartTime().toLocalDate();
        if (holidayPort.isHoliday(bookingDay)) {
            throw new IllegalArgumentException("Bookings are not allowed on public holidays");
        }

        // 2) Find room
        var room = studyRoomRepository.findById(req.getStudyRoomId())
                .orElseThrow(() -> new IllegalArgumentException("StudyRoom not found: " + req.getStudyRoomId()));

        if (!room.isAvailable()) {
            throw new IllegalArgumentException("StudyRoom is not available");
        }

        // 3) Find current user from JWT
        String email = currentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        // 4) Rule: no overlaps for same room
        long overlaps = bookingRepository.countOverlaps(room.getId(), req.getStartTime(), req.getEndTime());
        if (overlaps > 0) {
            throw new IllegalArgumentException("Room is already booked for this time range");
        }

        // 5) Rule: max X bookings per day (for this user)
        LocalDate day = req.getStartTime().toLocalDate();
        LocalDateTime from = day.atStartOfDay();
        LocalDateTime to = day.plusDays(1).atStartOfDay();

        long todayCount = bookingRepository.countForUserInRange(email, from, to);
        if (todayCount >= MAX_BOOKINGS_PER_DAY) {
            throw new IllegalArgumentException("Max bookings per day exceeded (" + MAX_BOOKINGS_PER_DAY + ")");
        }

        // 6) Save
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStudentName(req.getStudentName() != null ? req.getStudentName() : email);
        booking.setStartTime(req.getStartTime());
        booking.setEndTime(req.getEndTime());
        booking.setStudyRoom(room);

        Booking saved = bookingRepository.save(booking);



        return saved;
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("No authenticated user");
        }
        return auth.getName();
    }
}
