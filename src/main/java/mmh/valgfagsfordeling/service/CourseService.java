package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.dto.CourseDTO;
import mmh.valgfagsfordeling.model.Course;
import mmh.valgfagsfordeling.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    // Map der holder alle valgfag efter preload, så der nemt/hurtigt kan søges på courseId og opdateres korrekt valgfag
    private Map<Integer, Course> courseCache = new HashMap<>();

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    //Metoder til at hente alle valgfag på én gang inden de skal bruges ifm fordelingen, så der ikke
    //sker databasekald op til flere gange pr elev. Listen ændres til HashMap
    public void preloadAllCourses() {
        List<Course> allCourses = courseRepository.findAll();
        courseCache = allCourses.stream()
                .collect(Collectors.toMap(
                        course -> course.getCourseId(), //courseId bliver nøglen
                        course -> course  //værdi er valgfagsobjektet
                ));
    }


    //-----------Entity metoder---------------

    public Course getCourse(int courseId) {
        if (courseCache.containsKey(courseId)) {  //henter valgfag fra HashMap (ingen opslag i db nødvendigt)
            return courseCache.get(courseId);
        }
        //Hvis valgfaget af en eller anden grund ikke er preloaded, så laves opslag i db og gemmes i HashMap
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Valgfag ikke fundet: " + courseId));
        courseCache.put(courseId, course);
        return course;
    }

    public boolean checkIfAvailable(int courseId) {
        Course selectedCourse = getCourse(courseId);
        return selectedCourse.getParticipantsCount() < selectedCourse.getMaxParticipants();
    }

    public void addCount(int courseId) {
        Course selectedCourse = getCourse(courseId);
        selectedCourse.incrementParticipantsCount();
    }

    //-------------DTO------------------

    public CourseDTO getCourseDTO(int courseId) {
        Course course = getCourse(courseId);
        return convertToDTO(course);
    }

    public List<CourseDTO> getAllCoursesDTO() {
        return courseRepository.findAll().stream()
                .map(course -> convertToDTO(course))
                .toList();

    }

    public List<CourseDTO> getAllCoursesSpecificStudent(int studentId) {
        return courseRepository.findByStudentStundetId(studentId).stream()
                .map(course -> convertToDTO(course))
                .toList();
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
