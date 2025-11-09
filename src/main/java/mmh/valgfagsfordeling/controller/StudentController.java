package mmh.valgfagsfordeling.controller;

import mmh.valgfagsfordeling.dto.StudentDTO;
import mmh.valgfagsfordeling.service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/student")
    public List<StudentDTO> allStudent() {
        return studentService.allStudents();
    }

    @GetMapping("/student/{id}")
    public StudentDTO getStudent(@PathVariable int id) {
        return studentService.getStudent(id);
    }
}
