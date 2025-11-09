package mmh.valgfagsfordeling.repository;

import mmh.valgfagsfordeling.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Integer> {

    List<Priority> findByStudentStudentIdOrderByPriorityNumberAsc(Integer studentId);

}
