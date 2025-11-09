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

    //-----------CRUD Entities---------------

    public List<Student> studentListInternal() {
        return studentRepository.findAll();
    }

    public Student getStudent(int studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Elev ikke fundet"));
    }

    //--------------DTO--------------------

    public List<StudentDTO> allStudentsDTO() {
        return studentListInternal().stream()
                .map(student -> convertToDTO(student))
                .toList();
    }

    public StudentDTO getStudentDTO(int studentId) {
        return convertToDTO(getStudent(studentId));
    }



    //-------------Hjælpemetoder---------------

    public StudentDTO convertToDTO(Student student) {
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
