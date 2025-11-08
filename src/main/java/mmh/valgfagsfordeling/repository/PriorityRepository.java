package mmh.valgfagsfordeling.repository;

import mmh.valgfagsfordeling.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Integer> {

}
