package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.dto.PriorityDTO;
import mmh.valgfagsfordeling.model.Course;
import mmh.valgfagsfordeling.model.Priority;
import mmh.valgfagsfordeling.model.Student;
import mmh.valgfagsfordeling.repository.PriorityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriorityService {

    private final PriorityRepository priorityRepository;

    public PriorityService(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    //-----------------Entities-----------------------

    public List<Student> getListOfStudentsForSpecificCourse(int courseId) {
        return priorityRepository.findByCourseCourseIdAndFulfilledTrue(courseId).stream()
                .map(priority -> priority.getStudent())
                .distinct() // sikrer at hver elev kun optræder én gang (ingen dubletter)
                .toList();
    }

    public List<Course> getListOfCoursesForSpecificStudent(int studentId) {
        List<Priority> priorities = priorityRepository.findByStudentStudentIdAndFulfilledTrue(studentId);

        // 🧠 Debug: Udskriv alle kurser som bliver fundet for denne elev
        priorities.forEach(p ->
                System.out.println(
                        "Student " + studentId + " → Course: "
                                + p.getCourse().getCourseName()
                                + " (priorityNumber=" + p.getPriorityNumber() + ", fulfilled=" + p.isFulfilled() + ")"
                )
        );

        // Returner listen af unikke kurser
        return priorities.stream()
                .map(Priority::getCourse)
                .distinct()
                .toList();
    }




//    public List<Course> getListOfCoursesForSpecificStudent(int studentId) {
//        return priorityRepository.findByStudentStudentIdAndFulfilledTrue(studentId).stream()
//                .map(priority -> priority.getCourse())
//                .distinct()
//                .toList();
//    }


    public List<Priority> allPrioritiesSpecificStudent(int studentId) {
        return priorityRepository.findByStudentStudentIdOrderByPriorityNumberAsc(studentId);
    }

    //-------------------DTO--------------------------

    public List<PriorityDTO> allPrioritiesDTO() {
        return priorityRepository.findAll().stream()
                .map(priority -> convertToDTO(priority))
                .toList();
    }

    public List<PriorityDTO> allPrioritiesSpecificStudentDTO(int studentId) {
        return allPrioritiesSpecificStudent(studentId).stream()
                .map(priority -> convertToDTO(priority))
                .toList();
    }



    //-------------Hjælpemetoder---------------

    private PriorityDTO convertToDTO(Priority priority) {
        PriorityDTO dto = new PriorityDTO();
        dto.setPriorityId(priority.getPriorityId());
        dto.setPriorityNumber(priority.getPriorityNumber());
        dto.setFulfilled(priority.isFulfilled());
        dto.setCourseName(priority.getCourse().getCourseName());
        return dto;
    }
}
