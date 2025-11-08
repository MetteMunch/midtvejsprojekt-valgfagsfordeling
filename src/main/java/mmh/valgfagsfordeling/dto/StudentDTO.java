package mmh.valgfagsfordeling.dto;

import java.util.List;

public class StudentDTO {

    private int studentId;
    private String fullName;
    private String email;
    private List<PriorityDTO> priorities;

    public StudentDTO() {
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PriorityDTO> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<PriorityDTO> priorities) {
        this.priorities = priorities;
    }
}
