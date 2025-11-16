package mmh.valgfagsfordeling.dto;

import java.util.List;
import java.util.Map;

public class DashboardAdmDTO {

    public int processedStudents;
    public int totalQuantification;
    public Map<String, Integer> stats;
    public List<StudentDTO> studentsWithoutPriorities;
    public List<StatsDTO> courseStats;

    public DashboardAdmDTO() {
    }

    public int getProcessedStudents() {
        return processedStudents;
    }

    public void setProcessedStudents(int processedStudents) {
        this.processedStudents = processedStudents;
    }

    public int getTotalQuantification() {
        return totalQuantification;
    }

    public void setTotalQuantification(int totalQuantification) {
        this.totalQuantification = totalQuantification;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }

    public void setStats(Map<String, Integer> stats) {
        this.stats = stats;
    }

    public List<StudentDTO> getStudentsWithoutPriorities() {
        return studentsWithoutPriorities;
    }

    public void setStudentsWithoutPriorities(List<StudentDTO> studentsWithoutPriorities) {
        this.studentsWithoutPriorities = studentsWithoutPriorities;
    }

    public List<StatsDTO> getCourseStats() {
        return courseStats;
    }

    public void setCourseStats(List<StatsDTO> courseStats) {
        this.courseStats = courseStats;
    }
}
