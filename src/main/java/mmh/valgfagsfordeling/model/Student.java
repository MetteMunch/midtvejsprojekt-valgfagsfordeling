package mmh.valgfagsfordeling.model;

import java.util.List;

public class Student {

    private int studentID;
    private String fullName;
    private String email;
    private int handlingCount;
    private List<Priority> priorityList;

    public Student(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
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
