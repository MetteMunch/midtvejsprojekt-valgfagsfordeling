package mmh.valgfagsfordeling.repository;

import mmh.valgfagsfordeling.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    //List<Course> findByStudentStundetId(Integer studentId);

    @Modifying
    @Query("UPDATE Course c SET c.participantsCount = 0")
    void resetAllParticipantCounts();



}
