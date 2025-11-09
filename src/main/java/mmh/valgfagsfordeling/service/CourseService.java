package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.dto.CourseDTO;
import mmh.valgfagsfordeling.model.Course;
import mmh.valgfagsfordeling.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    //-----------Entity metoder---------------

    public Course getCourse(int courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("valgfag ikke fundet"));

    }

    public boolean checkIfAvailable(int courseId) {
        Course selectedCourse = getCourse(courseId);
        return selectedCourse.getParticipantsCount()< selectedCourse.getMaxParticipants();
    }

    public void addCount(int courseId) {
        Course selectedCourse = getCourse(courseId);
        selectedCourse.setParticipantsCount();
    }

    //-------------DTO------------------

    public CourseDTO getCourseDTO(int courseId) {
        Course course = getCourse(courseId);
        return convertToDTO(course);
    }


    //-------------Hjælpemetoder---------------

    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setCourseId(course.getCourseId());
        dto.setCourseName(course.getCourseName());
        dto.setDescription(course.getDescription());
        dto.setParticipantsCount(course.getParticipantsCount());
        dto.setMaxParticipants(course.getMaxParticipants());
        dto.setMinParticipants(course.getMinParticipants());
        dto.setSemester(course.getSemester());
        dto.setTeacher(course.getTeacher());
        return dto;
    }
}
