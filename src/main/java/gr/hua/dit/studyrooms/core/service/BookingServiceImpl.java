package gr.hua.dit.studyrooms.core.service;


import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.model.User;
import gr.hua.dit.studyrooms.core.port.HolidayPort;
import gr.hua.dit.studyrooms.core.repository.BookingRepository;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.repository.UserRepository;
import gr.hua.dit.studyrooms.core.service.dto.SlotAvailability;
import gr.hua.dit.studyrooms.web.rest.model.BookingCreateRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingServiceImpl implements BookingService {

    private static final int MAX_BOOKINGS_PER_DAY = 2;

    private final BookingRepository bookingRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final UserRepository userRepository;
    private final HolidayPort holidayPort;

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

        // 2) Rule: no bookings on public holidays
        LocalDate bookingDay = req.getStartTime().toLocalDate();
        if (holidayPort.isHoliday(bookingDay)) {
            throw new IllegalArgumentException("Bookings are not allowed on public holidays");
        }

        // 3) Find room
        var room = studyRoomRepository.findById(req.getStudyRoomId())
                .orElseThrow(() -> new IllegalArgumentException("StudyRoom not found: " + req.getStudyRoomId()));

        if (!room.isAvailable()) {
            throw new IllegalArgumentException("StudyRoom is not available");
        }

        // 4) Find current user
        String email = currentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        // 5) Rule: max X bookings per day (for this user)
        LocalDate day = req.getStartTime().toLocalDate();
        LocalDateTime from = day.atStartOfDay();
        LocalDateTime to = day.plusDays(1).atStartOfDay();

        long todayCount = bookingRepository.countForUserInRange(email, from, to);
        if (todayCount >= MAX_BOOKINGS_PER_DAY) {
            throw new IllegalArgumentException("Max bookings per day exceeded (" + MAX_BOOKINGS_PER_DAY + ")");
        }

        // ✅ 6) Seat-based availability (Β): many bookings per slot until capacity is full
        // Each booking consumes 1 seat
        int bookedSeats = bookingRepository.sumBookedSeats(room.getId(), req.getStartTime(), req.getEndTime());
        int freeSeats = room.getCapacity() - bookedSeats;

        if (freeSeats <= 0) {
            throw new IllegalArgumentException("No available seats for this time slot");
        }

        // 7) Save booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStudentName(req.getStudentName() != null ? req.getStudentName() : email);
        booking.setStartTime(req.getStartTime());
        booking.setEndTime(req.getEndTime());
        booking.setStudyRoom(room);

        // ✅ IMPORTANT: each booking occupies 1 seat
        booking.setSeats(1);

        return bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    // ✅ Calendar: returns availability per day with free seats per slot
    @Override
    public Map<LocalDate, List<SlotAvailability>> getAvailabilityCalendar(Long roomId) {

        final int DAYS_AHEAD = 7;
        final int OPEN_HOUR = 8;
        final int CLOSE_HOUR = 20; // last start is 19:00
        final int SLOT_HOURS = 1;

        var room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("StudyRoom not found: " + roomId));

        if (!room.isAvailable()) return Map.of();

        Map<LocalDate, List<SlotAvailability>> out = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        for (int d = 0; d < DAYS_AHEAD; d++) {
            LocalDate date = today.plusDays(d);

            if (holidayPort.isHoliday(date)) continue;

            List<SlotAvailability> slots = new ArrayList<>();

            for (int h = OPEN_HOUR; h < CLOSE_HOUR; h++) {
                LocalDateTime start = date.atTime(h, 0);
                LocalDateTime end = start.plusHours(SLOT_HOURS);

                int booked = bookingRepository.sumBookedSeats(roomId, start, end);
                int free = room.getCapacity() - booked;

                if (free > 0) {
                    slots.add(new SlotAvailability(start, free));
                }
            }

            if (!slots.isEmpty()) {
                out.put(date, slots);
            }
        }

        return out;
    }

    // ✅ For rooms list: show "next slot" free seats
    @Override
    public SlotAvailability getNextSlotAvailability(Long roomId) {

        final int OPEN_HOUR = 8;
        final int CLOSE_HOUR = 20;

        var room = studyRoomRepository.findById(roomId).orElse(null);
        if (room == null || !room.isAvailable()) return null;

        LocalDateTime now = LocalDateTime.now();

        // round up to next hour
        LocalDateTime start = now.withMinute(0).withSecond(0).withNano(0);
        if (now.getMinute() > 0) start = start.plusHours(1);

        // within working hours
        if (start.getHour() < OPEN_HOUR) start = start.toLocalDate().atTime(OPEN_HOUR, 0);
        if (start.getHour() >= CLOSE_HOUR) start = start.toLocalDate().plusDays(1).atTime(OPEN_HOUR, 0);

        // skip holidays
        if (holidayPort.isHoliday(start.toLocalDate())) return null;

        LocalDateTime end = start.plusHours(1);

        int booked = bookingRepository.sumBookedSeats(roomId, start, end);
        int free = room.getCapacity() - booked;

        return new SlotAvailability(start, Math.max(free, 0));
    }

    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("No authenticated user");
        }
        return auth.getName();
    }
}
