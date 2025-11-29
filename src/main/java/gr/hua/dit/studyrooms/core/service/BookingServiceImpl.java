package gr.hua.dit.studyrooms.core.service;


import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getBookingById(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.orElse(null);
    }

    @Override
    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
