package mmh.valgfagsfordeling.controller;

import mmh.valgfagsfordeling.dto.PriorityDTO;
import mmh.valgfagsfordeling.service.PriorityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PriorityController {

    private final PriorityService priorityService;

    public PriorityController(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @GetMapping("/priority/{studentid}")
    public ResponseEntity<List<PriorityDTO>> allPrioritiesSpecificStudent(@PathVariable int studentid) {
        List<PriorityDTO> priorities = priorityService.allPrioritiesSpecificStudentDTO(studentid);

        if (priorities.isEmpty()) {
            return ResponseEntity.noContent().build(); //returnerer statuskode 204
        }
        return ResponseEntity.ok(priorities);
    }


}
