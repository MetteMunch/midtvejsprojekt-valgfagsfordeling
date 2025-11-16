package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.dto.*;
import mmh.valgfagsfordeling.model.Priority;
import mmh.valgfagsfordeling.model.Student;
import mmh.valgfagsfordeling.model.Course;
import mmh.valgfagsfordeling.model.Teacher;
import mmh.valgfagsfordeling.repository.CourseRepository;
import mmh.valgfagsfordeling.repository.PriorityRepository;
import mmh.valgfagsfordeling.repository.StudentRepository;
import mmh.valgfagsfordeling.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdministrationService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PriorityRepository priorityRepository;
    private final TeacherRepository teacherRepository;

    // Map der holder alle valgfag efter preload, så der nemt/hurtigt kan søges på courseId og opdateres korrekt valgfag
    private Map<Integer, Course> courseCache = new HashMap<>();


    private List<Student> initListAllStudents;
    private List<Student> allStudentListBackUp;
    private List<Student> toBeFirstList1;
    private List<Student> fulfilled1;
    private List<Student> toBeFirstList2;
    private List<Student> fulfilled2;
    private List<Student> toBeManualHandled;

    public AdministrationService(StudentRepository studentRepository, CourseRepository courseRepository, PriorityRepository priorityRepository, TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.priorityRepository = priorityRepository;
        this.teacherRepository = teacherRepository;
    }

    //--------- FORDELINGSALGORITMEN-------------

    @Transactional
    public void distributionGreedyWithFairness() {

        preloadAllCourses(); //starter med at hente alle valgfag gemt i HashMap
        //så der ikke skal laves db opslag flere gange pr elev


        initListAllStudents = allStudentsWithPriorities();
        allStudentListBackUp = new ArrayList<>(initListAllStudents); //Vi gemmer lige opstartslisten, så vi har denne til kvantificering mm
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

    //--------------------Hjælpemetoder til algoritmen--------------


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

                if (checkIfAvailable(course.getCourseId())) {
                    p.setFulfilled(true);
                    student.incrementHandlingCount();
                    System.out.println("hvis første bliver fulfilled handling count er: " + student.getHandlingCount());
                    addCount(course.getCourseId());
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
    //--------------KVANTIFICERINGS- OG STATISTIKMETODER---------

    public int getTotalProcessedStudents() {
        return (allStudentListBackUp.isEmpty()) ? 0 : allStudentListBackUp.size();
    }

    public int calculateStudentScore(Student student) {
        List<Priority> priorities = student.getPriorityList();

        long fulfilledFirst3 = priorities.stream()
                .filter(p -> p.getPriorityNumber() <= 3)
                .filter(Priority::isFulfilled)
                .count();

        if (fulfilledFirst3 == 3) return 0;
        if (fulfilledFirst3 == 2) return -1;
        if (fulfilledFirst3 == 1) return -2;
        return -3;
    }

    public int getTotalQuantificationScore() {
        return allStudentListBackUp.stream()
                .mapToInt(this::calculateStudentScore)
                .sum();
    }

    //Metode der returnerer de data som jeg gerne vil vise på dashboard i forhold til kvantificering
    public Map<String, Integer> getDistributionStats() {

        Map<String, Integer> stats = new HashMap<>(); //key er kategorien og værdien er antal elever i den kategori

        stats.put("threeCourses", (int) allStudentListBackUp.stream()
                .filter(s -> getFulfilledCount(s) == 3) //Hvis eleven har fået præcis 3 valgfag tildelt så kommer eleven med videre
                .count()); //sammentælling af alle elever med præcis 3 tildelte valgfag

        stats.put("twoCourses", (int) allStudentListBackUp.stream()
                .filter(s -> getFulfilledCount(s) == 2)
                .count());

        stats.put("oneCourse", (int) allStudentListBackUp.stream()
                .filter(s -> getFulfilledCount(s) == 1)
                .count());

        stats.put("zeroCourses", (int) allStudentListBackUp.stream()
                .filter(s -> getFulfilledCount(s) == 0)
                .count());

        return stats;
    }

    //Metode der returnerer hvor mange valgfag en elev har fået tildelt
    private int getFulfilledCount(Student s) {
        return (int) s.getPriorityList().stream()
                .filter(Priority::isFulfilled)
                .count();
    }

    //Metode der returnerer liste med valgfag og antal tilmeldte
    public List<StatsDTO> getCourseStats() {
        return courseCache.values().stream()
                .map(c -> new StatsDTO(c.getCourseName(), c.getParticipantsCount()))
                .toList();
    }


    //--------------------DTO METODER TIL ENDPOINTS-----------------------

    //samling af data til dashboard for studieadministration
    public DashboardAdmDTO buildDashboard() {
        DashboardAdmDTO dashboardData = new DashboardAdmDTO();
        dashboardData.setProcessedStudents(getTotalProcessedStudents());
        dashboardData.setTotalQuantification(getTotalQuantificationScore());
        dashboardData.setStats(getDistributionStats());
        dashboardData.setStudentsWithoutPriorities(allStudentsWithoutPrioritiesDTO());
        dashboardData.setCourseStats(getCourseStats());

        return dashboardData;
    }


    //liste over alle elever på et givent valgfag (efter fordeling)
    public List<StudentDTO> listOfStudentsSpecificCourseDTO(int courseId) {
        List<Student> students = getListOfStudentsForSpecificCourse(courseId);
        return students.stream()
                .map(student -> convertStudentToDTO(student))
                .toList();
    }

    //liste der viser valgfag for en given elev (efter fordeling)
    public List<CourseDTO> listOfCoursesSpecificStudent(int studentId) {
        List<Course> courses = getListOfCoursesForSpecificStudent(studentId);
        return courses.stream()
                .map(course -> convertCourseToDTO(course))
                .toList();
    }

    //liste med de elever, som manuelt skal håndteres (efter fordeling)
    public List<StudentDTO> toBeManualHandledListDTO() {
        return toBeManualHandled.stream()
                .map(student -> convertStudentToDTO(student))
                .toList();
    }


    //-----------------STUDENT metoder--------------

    public List<Student> allStudents() {
        return studentRepository.findAll();
    }

    public List<Student> allStudentsWithPriorities() {
        return allStudents().stream()
                .filter(student -> !student.getPriorityList().isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
        //Her kan jeg ikke bare bruge .toList, da jeg skal bruge en manipulerbar liste (hvor jeg kan bruge .remove metoden)
    }

    public List<Student> studentsWithoutPrioritiesHandedIn() {
        return allStudents().stream()
                .filter(student -> student.getPriorityList() == null || student.getPriorityList().isEmpty())
                .toList();
    }

    public Student getStudent(int studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Elev ikke fundet"));
    }

    public List<StudentDTO> allStudentsDTO() {
        return allStudents().stream()
                .map(student -> convertStudentToDTO(student))
                .toList();
    }

    public List<StudentDTO> allStudentsWithPrioritiesDTO() {
        return allStudentsWithPriorities().stream()
                .map(student -> convertStudentToDTO(student))
                .toList();
    }

    public List<StudentDTO> allStudentsWithoutPrioritiesDTO() {
        return studentsWithoutPrioritiesHandedIn().stream()
                .map(student -> convertStudentToDTO(student))
                .toList();
    }

    public StudentDTO getStudentDTO(int studentId) {
        return convertStudentToDTO(getStudent(studentId));
    }

    public StudentDTO convertStudentToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId(student.getStudentId());
        dto.setFullName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setPriorities(
                student.getPriorityList().stream()
                        .map(p -> {
                            PriorityDTO pDTO = new PriorityDTO();
                            pDTO.setPriorityId(p.getPriorityId());
                            pDTO.setPriorityNumber(p.getPriorityNumber());
                            pDTO.setFulfilled(p.isFulfilled());
                            pDTO.setCourseName(p.getCourse().getCourseName());
                            return pDTO;
                        }).toList()
        );
        return dto;
    }


    //------------------COURSE metoder--------------

    //Metoder til at hente alle valgfag på én gang inden de skal bruges ifm fordelingen, så der ikke
    //sker databasekald op til flere gange pr elev. Listen ændres til HashMap
    public void preloadAllCourses() {
        List<Course> allCourses = courseRepository.findAll();
        courseCache = allCourses.stream()
                .collect(Collectors.toMap(
                        course -> course.getCourseId(), //courseId bliver nøglen
                        course -> course  //værdi er valgfagsobjektet
                ));
    }

    public Course getCourse(int courseId) {
        if (courseCache.containsKey(courseId)) {  //henter valgfag fra HashMap (ingen opslag i db nødvendigt)
            return courseCache.get(courseId);
        }
        //Hvis valgfaget af en eller anden grund ikke er preloaded, så laves opslag i db og gemmes i HashMap
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Valgfag ikke fundet: " + courseId));
        courseCache.put(courseId, course);
        return course;
    }

    public boolean checkIfAvailable(int courseId) {
        Course selectedCourse = getCourse(courseId);
        return selectedCourse.getParticipantsCount() < selectedCourse.getMaxParticipants();
    }

    public void addCount(int courseId) {
        Course selectedCourse = getCourse(courseId);
        selectedCourse.incrementParticipantsCount();
    }

    public CourseDTO getCourseDTO(int courseId) {
        Course course = getCourse(courseId);
        return convertCourseToDTO(course);
    }

    public List<CourseDTO> getAllCoursesDTO() {
        return courseRepository.findAll().stream()
                .map(course -> convertCourseToDTO(course))
                .toList();

    }

    public CourseDTO convertCourseToDTO(Course course) {
        TeacherDTO teacherDTO = convertTeacherToDTO(course.getTeacher());
        CourseDTO dto = new CourseDTO();
        dto.setCourseId(course.getCourseId());
        dto.setCourseName(course.getCourseName());
        dto.setDescription(course.getDescription());
        dto.setParticipantsCount(course.getParticipantsCount());
        dto.setMaxParticipants(course.getMaxParticipants());
        dto.setMinParticipants(course.getMinParticipants());
        dto.setSemester(course.getSemester());
        dto.setTeacher(teacherDTO);
        return dto;
    }
    //-----------------PRIORITY metoder ----------------------

    public List<Student> getListOfStudentsForSpecificCourse(int courseId) {
        return priorityRepository.findByCourseCourseIdAndFulfilledTrue(courseId).stream()
                .map(priority -> priority.getStudent())
                .distinct() // sikrer at hver elev kun optræder én gang (ingen dubletter)
                .toList();
    }

    public List<Course> getListOfCoursesForSpecificStudent(int studentId) {
        return priorityRepository.findByStudentStudentIdAndFulfilledTrue(studentId).stream()
                .map(priority -> priority.getCourse())
                .distinct()
                .toList();
    }


    public List<Priority> allPrioritiesSpecificStudent(int studentId) {
        return priorityRepository.findByStudentStudentIdOrderByPriorityNumberAsc(studentId);
    }

    public List<PriorityDTO> allPrioritiesDTO() {
        return priorityRepository.findAll().stream()
                .map(priority -> convertPriorityToDTO(priority))
                .toList();
    }

    public List<PriorityDTO> allPrioritiesSpecificStudentDTO(int studentId) {
        return allPrioritiesSpecificStudent(studentId).stream()
                .map(priority -> convertPriorityToDTO(priority))
                .toList();
    }

    private PriorityDTO convertPriorityToDTO(Priority priority) {
        PriorityDTO dto = new PriorityDTO();
        dto.setPriorityId(priority.getPriorityId());
        dto.setPriorityNumber(priority.getPriorityNumber());
        dto.setFulfilled(priority.isFulfilled());
        dto.setCourseName(priority.getCourse().getCourseName());
        return dto;
    }


    //------------------TEACHER metoder---------------------

    public TeacherDTO convertTeacherToDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();
        dto.setTeacherId(teacher.getTeacherId());
        dto.setTeacherFullName(teacher.getTeacherFullName());
        return dto;
    }


}
