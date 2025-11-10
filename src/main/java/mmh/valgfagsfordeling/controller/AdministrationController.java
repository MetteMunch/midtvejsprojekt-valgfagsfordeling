package mmh.valgfagsfordeling.controller;

import mmh.valgfagsfordeling.dto.StudentDTO;
import mmh.valgfagsfordeling.service.AdministrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdministrationController {

    private final AdministrationService administrationService;

    public AdministrationController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startDistribution() {
        administrationService.distributionGreedyWithFairness();
        return ResponseEntity.accepted()
                .body("Fordelingsalgoritme igangsat");
    }

    @GetMapping("/manual")
    public ResponseEntity<List<StudentDTO>> toBeManualHandledList() {
        List<StudentDTO> students = administrationService.toBeManualHandledListDTO();

        if(students.isEmpty()) {
            return ResponseEntity.noContent().build(); //returnerer statuskode 204
        }

        return ResponseEntity.ok(students); //returnerer statuskode 200 og listen med students
    }





}
