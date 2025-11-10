package mmh.valgfagsfordeling.repository;

import mmh.valgfagsfordeling.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Integer> {

    //Query til at finde alle prioriteter for en given elev sorteret efter prioritet
    List<Priority> findByStudentStudentIdOrderByPriorityNumberAsc(int studentId);

    //Query til at finde alle elever som har fået plads på et givent valgfag
    List<Priority> findByCourseCourseIdAndFulfilledTrue(int courseId);

    //Query til at finde alle tildelte valgfag for en given elev
    List<Priority> findByStudentStudentIdAndFulfilledTrue(int studentId);

}
