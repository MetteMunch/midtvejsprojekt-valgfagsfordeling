package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.dto.CourseDTO;
import mmh.valgfagsfordeling.dto.TeacherDTO;
import mmh.valgfagsfordeling.model.Teacher;
import mmh.valgfagsfordeling.repository.TeacherRepository;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }


    //-------------Hjælpemetoder---------------

    private TeacherDTO convertToDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();
        dto.setTeacherId(teacher.getTeacherId());
        dto.setTeacherFullName(teacher.getTeacherFullName());
        dto.setCourses(
                teacher.getCourseList().stream()
                        .map(c -> {
                            CourseDTO cDTO = new CourseDTO();
                            cDTO.setCourseId(c.getCourseId());
                            cDTO.setCourseName(c.getCourseName());
                            cDTO.setDescription(c.getDescription());
                            cDTO.setTeacher(c.getTeacher());
                            cDTO.setSemester(c.getSemester());
                            cDTO.setMaxParticipants(c.getMaxParticipants());
                            cDTO.setMinParticipants(c.getMinParticipants());
                            cDTO.setParticipantsCount(c.getParticipantsCount());
                            return cDTO;
                        }).toList()
        );
        return dto;
    }
}
