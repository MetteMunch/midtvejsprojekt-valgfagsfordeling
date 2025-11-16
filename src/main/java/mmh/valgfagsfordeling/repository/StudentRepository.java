package mmh.valgfagsfordeling.repository;

import mmh.valgfagsfordeling.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Modifying
    @Query("UPDATE Student s SET s.handlingCount = 0")
    void resetAllHandlingCounts();


}
