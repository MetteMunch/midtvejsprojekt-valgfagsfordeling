package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.dto.PriorityDTO;
import mmh.valgfagsfordeling.model.Priority;
import mmh.valgfagsfordeling.repository.PriorityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriorityService {

    private final PriorityRepository priorityRepository;

    public PriorityService(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    //--------------CRUD Entities-----------------------


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
