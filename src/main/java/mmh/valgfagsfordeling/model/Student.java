package mmh.valgfagsfordeling.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;
    private String fullName;
    private String email;
    private int handlingCount = 0;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Priority> priorityList;

    public Student(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public Student() {
    }

    public void incrementHandlingCount() {
        this.handlingCount++;
    }


    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentID) {
        this.studentId = studentID;
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

    public int getHandlingCount() {
        return handlingCount;
    }

    public void setHandlingCount(int handlingCount) {
        this.handlingCount = handlingCount;
    }

    public List<Priority> getPriorityList() {
        return priorityList;
    }

    public void setPriorityList(List<Priority> priorityList) {
        this.priorityList = priorityList;
    }
}
