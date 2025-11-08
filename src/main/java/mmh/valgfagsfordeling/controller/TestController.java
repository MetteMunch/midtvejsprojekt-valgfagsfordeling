package mmh.valgfagsfordeling.controller;

import mmh.valgfagsfordeling.dto.PriorityDTO;
import mmh.valgfagsfordeling.dto.StudentDTO;
import mmh.valgfagsfordeling.model.Student;
import mmh.valgfagsfordeling.repository.StudentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    private StudentRepository repo;

    public TestController(StudentRepository repo) {
        this.repo = repo;
    }

    /*------------TEST-----------------*/

    @GetMapping("/student")
    public List<StudentDTO> all() {
        return repo.findAll().stream() //findAll returnerer en liste af student, som vi itererer igennem med stream
                .map(student -> convertToDTO(student)) //.map() tager fat i hver element i stream, og kører det igennem funktionen (svarer til forEach loop)
                .toList(); //Når stream er færdigbehandlet omdannes til liste igen, nu af studentDTO'er
    }

    //TODO: skal flyttes til service-klasse
    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId(student.getStudentId());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setPriorities(
                student.getPriorityList().stream()
                        .map(p -> {
                            PriorityDTO pDto = new PriorityDTO();
                            pDto.setPriorityId(p.getPriorityId());
                            pDto.setPriorityNumber(p.getPriorityNumber());
                            pDto.setFulfilled(p.isFulfilled());
                            pDto.setCourseName(p.getCourse().getCourseName());
                            return pDto;
                        }).toList()
        );
        return dto;
    }


}
