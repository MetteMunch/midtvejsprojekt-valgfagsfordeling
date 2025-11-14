package mmh.valgfagsfordeling.controller;

import mmh.valgfagsfordeling.dto.CourseDTO;
import mmh.valgfagsfordeling.dto.DashboardAdmDTO;
import mmh.valgfagsfordeling.dto.StudentDTO;
import mmh.valgfagsfordeling.service.AdministrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdministrationController {

    private final AdministrationService administrationService;

    public AdministrationController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardAdmDTO> getDashboard() {
        DashboardAdmDTO dashboard = administrationService.buildDashboard();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/course/{id}/students")
    public ResponseEntity<List<StudentDTO>> listOfStudentsSpecificCourse(@PathVariable int id) {
        List<StudentDTO> students = administrationService.listOfStudentsSpecificCourseDTO(id);

        if (students.isEmpty()) {
            return ResponseEntity.noContent().build(); //returnerer statuskode 204
        }

        return ResponseEntity.ok(students); //returnerer statuskode 200 og listen med students
    }


    @GetMapping("/student/{id}/courses")
    public ResponseEntity<List<CourseDTO>> listOfCoursesSpecificStudent(@PathVariable int id) {
        List<CourseDTO> courses = administrationService.listOfCoursesSpecificStudent(id);

        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build(); //returnerer statuskode 204
        }
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/manual")
    public ResponseEntity<List<StudentDTO>> toBeManualHandledList() {
        List<StudentDTO> students = administrationService.toBeManualHandledListDTO();

        if (students.isEmpty()) {
            return ResponseEntity.noContent().build(); //returnerer statuskode 204
        }

        return ResponseEntity.ok(students); //returnerer statuskode 200 og listen med students
    }

    @PostMapping("/start")
    public ResponseEntity<String> startDistribution() {
        administrationService.distributionGreedyWithFairness();
        return ResponseEntity.accepted()
                .body("Fordelingsalgoritme igangsat");
    }



}
