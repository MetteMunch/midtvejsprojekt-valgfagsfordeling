package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.dto.PriorityDTO;
import mmh.valgfagsfordeling.dto.StudentDTO;
import mmh.valgfagsfordeling.model.Student;
import mmh.valgfagsfordeling.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> studentListInternal() {
        return studentRepository.findAll();
    }

    public List<StudentDTO> allStudents() {
        return studentRepository.findAll().stream()
                .map(student -> convertToDTO(student))
                .toList();
    }

    public StudentDTO getStudent(int studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Elev ikke fundet"));
        return convertToDTO(student);
    }



    //-------------Hjælpemetoder---------------

    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId(student.getStudentId());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setPriorities(
                student.getPriorityList().stream()
                        .map(p -> {
                            PriorityDTO pDTO = new PriorityDTO();
                            pDTO.setPriorityId(p.getPriorityId());
                            pDTO.setPriorityNumber(p.getPriorityNumber());
                            pDTO.setFulfilled(p.isFulfilled());
                            pDTO.setCourseName(p.getCourse().getCourseName());
                            return pDTO;
                        }).toList()
        );
        return dto;
    }
}
