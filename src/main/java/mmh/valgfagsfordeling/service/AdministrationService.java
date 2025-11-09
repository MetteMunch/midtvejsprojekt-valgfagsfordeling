package mmh.valgfagsfordeling.service;

import jakarta.transaction.Transactional;
import mmh.valgfagsfordeling.dto.StudentDTO;
import mmh.valgfagsfordeling.model.Priority;
import mmh.valgfagsfordeling.model.Student;
import mmh.valgfagsfordeling.model.Course;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class AdministrationService {

    private final StudentService studentService;
    private final CourseService courseService;
    private final PriorityService priorityService;

    private List<Student> initListAllStudents;
    private List<Student> toBeFirstList1;
    private List<Student> fulfilled1;
    private List<Student> toBeFirstList2;
    private List<Student> fulfilled2;
    private List<Student> toBeManualHandled;

    public AdministrationService(StudentService studentService, CourseService courseService, PriorityService priorityService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.priorityService = priorityService;
    }


    public void distributionGreedyWithFairness() {
        initListAllStudents = studentService.studentListInternal();

        // Runde 1 - her gennemløbes hele opstartslisten med alle elever, og alle elever får tildelt ét valgfag efter
        // højeste mulige prioritet
        processRound(initListAllStudents, fulfilled1, toBeFirstList1, false);

        // Runde 2 - her gennemløbes først de elever, som ikke fik deres 1.prioritet opfyldt, og derefter de resterende
        processRound(toBeFirstList1, fulfilled2, toBeFirstList2, false);
        processRound(fulfilled1, fulfilled2, toBeFirstList2, false);

        // Runde 3 - her gennemløbes først de elever, som ikke fik deres 2.prioritet opfyldt, og derefter de resterende
        processRound(toBeFirstList2, new ArrayList<>(), new ArrayList<>(), true);
        processRound(fulfilled2, new ArrayList<>(), new ArrayList<>(), true);

    }


    //--------------------DTO-----------------------

    public List<StudentDTO> toBeManualHandledListDTO() {
        return toBeManualHandled.stream()
                .map(student -> studentService.convertToDTO(student))
                .toList();
    }

    //--------------------Hjælpemetoder--------------


    private void processRound(
            List<Student> sourceList,
            List<Student> fulfilledList,
            List<Student> toBeFirstList,
            boolean isFinalRound) {

        if (sourceList.isEmpty()) return;

        // Så længe der stadig er elever i kildelisten
        while (!sourceList.isEmpty()) {
            Student student = getRandomStudent(sourceList); // Fjerner eleven internt

            boolean gotCourse = false;

            // Gennemgå elevens prioriteter
            for (Priority p : student.getPriorityList()) {
                Course course = p.getCourse();

                if (courseService.checkIfAvailable(course.getCourseId())) {
                    p.setFulfilled(true);
                    student.setHandlingCount();
                    courseService.addCount(course.getCourseId());
                    gotCourse = true;

                    // Flyttes til rette liste (fairness)
                    if (p.getPriorityNumber() == 1) {
                        fulfilledList.add(student);
                    } else {
                        toBeFirstList.add(student);
                    }

                    break; // Elev fik et fag → stop prioritet-loop
                }
            }

            // Ingen fag kunne tildeles
            if (!gotCourse) {
                if (isFinalRound) {
                    toBeManualHandled.add(student);
                } else {
                    toBeFirstList.add(student);
                }
            }
        }
    }


    public Student getRandomStudent(List<Student> list) {
        Random random = new Random();
        int randomIndeks = random.nextInt(0, list.size());
        Student selectedStudent = list.get(randomIndeks);
        list.remove(randomIndeks); //her fjernes eleven fra listen
        return selectedStudent;
    }

}
