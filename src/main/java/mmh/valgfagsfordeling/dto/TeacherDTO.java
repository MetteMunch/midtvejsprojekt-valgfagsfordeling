package mmh.valgfagsfordeling.dto;

public class TeacherDTO {

    private int teacherId;
    private String teacherFullName;

    public TeacherDTO() {
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherFullName() {
        return teacherFullName;
    }

    public void setTeacherFullName(String teacherFullName) {
        this.teacherFullName = teacherFullName;
    }
}
