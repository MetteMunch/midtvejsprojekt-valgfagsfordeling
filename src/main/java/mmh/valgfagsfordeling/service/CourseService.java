package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.dto.CourseDTO;
import mmh.valgfagsfordeling.dto.PriorityDTO;
import mmh.valgfagsfordeling.model.Course;
import mmh.valgfagsfordeling.model.Priority;
import mmh.valgfagsfordeling.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
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
