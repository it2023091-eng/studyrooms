package gr.hua.dit.studyrooms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StudyroomsApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyroomsApplication.class, args);
    }


    @Bean
    public CommandLineRunner initData(StudyRoomRepository studyRoomRepository) {
        return args -> {
            if (studyRoomRepository.count() == 0) {
                studyRoomRepository.save(new StudyRoom("Room A1", 4, true));
                studyRoomRepository.save(new StudyRoom("Room B2", 6, true));
                studyRoomRepository.save(new StudyRoom("Silent Desk C3", 1, true));
            }
        };
    }
}
