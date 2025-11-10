package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.dto.CourseDTO;
import mmh.valgfagsfordeling.dto.StudentDTO;
import mmh.valgfagsfordeling.model.Priority;
import mmh.valgfagsfordeling.model.Student;
import mmh.valgfagsfordeling.model.Course;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional
    public void distributionGreedyWithFairness() {
        System.out.println("Her er jeg i metoden distributionGreedyWithFainess");
        courseService.preloadAllCourses(); //starter med at hente alle valgfag gemt i HashMap
        //så der ikke skal laves db opslag flere gange pr elev

        initListAllStudents = studentService.studentListInternal();
        toBeFirstList1 = new ArrayList<>();
        fulfilled1 = new ArrayList<>();
        toBeFirstList2 = new ArrayList<>();
        fulfilled2 = new ArrayList<>();
        toBeManualHandled = new ArrayList<>();

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
    //endpoint til at få liste over elever på et givent valgfag (efter fordeling)
    public List<StudentDTO> listOfStudentsSpecificCourseDTO(int courseId) {
        List<Student> students = priorityService.getListOfStudentsForSpecificCourse(courseId);
        return students.stream()
                .map(student -> studentService.convertToDTO(student))
                .toList();
    }

    //endpoint til at vise valgfag for en given elev (efter fordeling)
    public List<CourseDTO> listOfCoursesSpecificStudent(int studentId) {
        List<Course> courses = priorityService.getListOfCoursesForSpecificStudent(studentId);
        return courses.stream()
                .map(course -> courseService.convertToDTO(course))
                .toList();
    }

    //endpoint til at få en liste med de elever, som manuelt skal håndteres (efter fordeling)
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
        System.out.println("Her er jeg i metoden processRound");

        if (sourceList.isEmpty()) return;

        // Så længe der stadig er elever i kildelisten
        while (!sourceList.isEmpty()) {
            Student student = getRandomStudent(sourceList); // Fjerner eleven internt

            boolean gotCourse = false;

            int handlingCount = student.getHandlingCount(); //Hvor mange gange eleven er blevet håndteret (hvilken prioritet er vi kommet til)

            List<Priority> priorities = student.getPriorityList();

            // Gennemgå elevens prioriteter fra der hvor vi er kommet til
            for (int i = handlingCount; i < priorities.size(); i++) {
                Priority p = priorities.get(i);
                Course course = p.getCourse();
                System.out.println("Her er jeg inden første check og i / handlingCount er " + i);

                if (courseService.checkIfAvailable(course.getCourseId())) {
                    p.setFulfilled(true);
                    student.incrementHandlingCount();
                    System.out.println("hvis første bliver fulfilled handling count er: " + student.getHandlingCount());
                    courseService.addCount(course.getCourseId());
                    gotCourse = true;

                    // Flyttes til rette liste (fairness)
                    if (p.getPriorityNumber() == handlingCount) {
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
