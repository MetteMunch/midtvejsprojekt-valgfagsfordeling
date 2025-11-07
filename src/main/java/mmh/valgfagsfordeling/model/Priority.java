package mmh.valgfagsfordeling.model;

public class Priority {

    private int priorityId;
    private int priorityNumber;
    private boolean fullfilled;
    private Course course;

    public Priority(int priorityNumber, Course course) {
        this.priorityNumber = priorityNumber;
        this.course = course;
        fullfilled = false;
    }

    public int getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(int priorityId) {
        this.priorityId = priorityId;
    }

    public int getPriorityNumber() {
        return priorityNumber;
    }

    public void setPriorityNumber(int priorityNumber) {
        this.priorityNumber = priorityNumber;
    }

    public boolean isFullfilled() {
        return fullfilled;
    }

    public void setFullfilled(boolean fullfilled) {
        this.fullfilled = fullfilled;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
