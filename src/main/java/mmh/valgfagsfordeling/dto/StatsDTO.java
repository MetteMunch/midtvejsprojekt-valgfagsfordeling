package mmh.valgfagsfordeling.dto;

public class StatsDTO {

    private String courseName;
    private int participantCount;

    public StatsDTO(String courseName, int participantCount) {
        this.courseName = courseName;
        this.participantCount = participantCount;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }
}
