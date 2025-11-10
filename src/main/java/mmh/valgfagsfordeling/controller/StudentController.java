package mmh.valgfagsfordeling.controller;

import mmh.valgfagsfordeling.dto.StudentDTO;
import mmh.valgfagsfordeling.service.AdministrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);
    private final AdministrationService administrationService;

    public StudentController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @GetMapping("/student")
    public ResponseEntity<List<StudentDTO>> allStudent() {
        List<StudentDTO> allStudents = administrationService.allStudentsDTO();

        if(allStudents.isEmpty()) {
            return ResponseEntity.noContent().build(); //returnerer fejlkode 204
        }
        return ResponseEntity.ok(allStudents);
    }

//    @GetMapping("/student/{id}")
//    public ResponseEntity<StudentDTO> getStudent(@PathVariable int id) {
//        try {
//            StudentDTO student = studentService.getStudentDTO(id);
//            return ResponseEntity.ok(student);
//        } catch (RuntimeException e) {
//            log.error("Fejl ved hentning af elev med id {}: {}", id, e.getMessage(), e);
//            return ResponseEntity.notFound().build(); //returnerer fejlkode 404 til frontend
//        }
//    }
    }

