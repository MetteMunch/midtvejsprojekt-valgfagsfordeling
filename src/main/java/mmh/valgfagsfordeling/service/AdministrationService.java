package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.model.Student;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class AdministrationService {

    private final StudentService studentService;
    private final CourseService courseService;
    private final PriorityService priorityService;

    private List<Student> initListAllStudents;
    private List<Student> toBeFirstList1;
    private List<Student> fulfilled1;
    private List<Student> getToBeFirstList2;
    private List<Student> fulfilled2;

    public AdministrationService(StudentService studentService, CourseService courseService, PriorityService priorityService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.priorityService = priorityService;
    }

//    public void greedyWithFainessDistribution() {
//
//        if (!initListAllStudents.isEmpty()) {
//            Student selectedStudent = getRandomStudent(initListAllStudents);
//
//
//
//        }
//
//    }

    //--------------------Hjælpemetoder--------------

    public Student getRandomStudent(List<Student> list) {
        Random random = new Random();
        int randomIndeks = random.nextInt(0, list.size());
        Student selectedStudent = list.get(randomIndeks);
        return selectedStudent;
    }


}
