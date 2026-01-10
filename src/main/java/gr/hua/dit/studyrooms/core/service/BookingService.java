package gr.hua.dit.studyrooms.core.service;



import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.web.rest.model.BookingCreateRequest;

import java.util.List;

public interface BookingService {
    List<Booking> getAllBookings();
    Booking getBookingById(Long id);
    Booking createBooking(BookingCreateRequest req);
    void deleteBooking(Long id);

    // extra (αν το θες)
    List<Booking> getMyBookings();
}

