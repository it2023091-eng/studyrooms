package gr.hua.dit.studyrooms.core.service;



import gr.hua.dit.studyrooms.core.model.Booking;
import java.util.List;

public interface BookingService {
    List<Booking> getAllBookings();
    Booking getBookingById(Long id);
    Booking createBooking(Booking booking);
    void deleteBooking(Long id);
}
