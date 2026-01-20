package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.model.Course;
import mmh.valgfagsfordeling.model.Priority;
import mmh.valgfagsfordeling.model.Student;
import mmh.valgfagsfordeling.repository.CourseRepository;
import mmh.valgfagsfordeling.repository.PriorityRepository;
import mmh.valgfagsfordeling.repository.StudentRepository;
import mmh.valgfagsfordeling.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdministrationServiceTest {

    @Mock
    StudentRepository studentRepo;

    @Mock
    CourseRepository courseRepo;

    @Mock
    PriorityRepository priorityRepo;

    @Mock
    TeacherRepository teacherRepo;

    AdministrationService service;

    Student s1;
    Student s2;
    Student s3;
    Student s4;
    Student s5;
    Student s6;
    Course c1;
    Course c2;
    Course c3;
    Course c4;
    Course c5;
    Course c6;


    @BeforeEach
    void setUp() {
        service = new AdministrationService(studentRepo, courseRepo, priorityRepo, teacherRepo);
        service.setRandom(new Random(1));
        setUpTestData();
    }


    private Course course(int id, String name) {
        Course c = new Course();
        c.setCourseId(id);
        c.setCourseName(name);
        c.setMaxParticipants(1); //Her sættes hvert fag til max at kunne have én deltager
        c.setParticipantsCount(0);
        return c;
    }

    private Priority pr(Student s, Course c, int n) {
        Priority p = new Priority();
        p.setStudent(s);
        p.setCourse(c);
        p.setPriorityNumber(n);
        p.setFulfilled(false);
        return p;
    }

    private void setUpTestData() {

        s1 = new Student("A", "a@test.dk");
        s1.setStudentId(1);
        s2 = new Student("B", "b@test.dk");
        s2.setStudentId(2);
        s3 = new Student("C", "c@test.dk");
        s3.setStudentId(3);
        s4 = new Student("D", "d@test.dk");
        s4.setStudentId(4);
        s5 = new Student("E", "e@test.dk");
        s5.setStudentId(5);
        s6 = new Student("F", "f@test.dk");
        s6.setStudentId(6);


        c1 = course(1, "C1");
        c2 = course(2, "C2");
        c3 = course(3, "C3");
        c4 = course(4, "C4");
        c5 = course(5, "C5");
        c6 = course(6, "C6");

        s1.setPriorityList(List.of(
                pr(s1, c1, 1),
                pr(s1, c2, 2),
                pr(s1, c3, 3)
        ));

        s2.setPriorityList(List.of(
                pr(s2, c1, 1),
                pr(s2, c2, 2),
                pr(s2, c3, 3)
        ));

        s3.setPriorityList(List.of(
                pr(s3, c1, 1),
                pr(s3, c3, 2),
                pr(s3, c2, 3)
        ));

        s4.setPriorityList(List.of(
                pr(s4, c5, 1),
                pr(s4, c4, 2),
                pr(s4, c6, 3)
        ));

        s5.setPriorityList(List.of(
                pr(s5, c6, 1),
                pr(s5, c4, 2),
                pr(s5, c5, 3)
        ));

        s6.setPriorityList(List.of(
                pr(s6, c4, 1),
                pr(s6, c5, 2),
                pr(s6, c6, 3)
        ));
    }

    @Test
    void testDistributionBasicScenario() {

        // arrange
        when(courseRepo.findAll()).thenReturn(List.of(c1, c2, c3));
        when(studentRepo.findAll()).thenReturn(List.of(s1, s2, s3));

        // act
        service.distributionGreedyWithFairness();

        // assert
        // Validate course allocations:
        assertEquals(1, c1.getParticipantsCount());
        assertEquals(1, c2.getParticipantsCount());
        assertEquals(1, c3.getParticipantsCount());

        // All students got exactly 1 course after round 1
        assertEquals(3, service.getDistributionStats().get("oneCourse"));

        // HandlingCount must be 1 for all
        assertEquals(1, s1.getHandlingCount());
        assertEquals(1, s2.getHandlingCount());
        assertEquals(1, s3.getHandlingCount());

        // Fulfilled flags: exactly 1 fulfilled per student
        assertEquals(1, service.getFulfilledCount(s1));
        assertEquals(1, service.getFulfilledCount(s2));
        assertEquals(1, service.getFulfilledCount(s3));
    }

    @Test
    void testCheckIfAvailable() {
        c6.setMaxParticipants(2);
        c6.setParticipantsCount(1);

        // Læg i cache manuelt (for at undgå DB)
        service.getCourseCache().put(6, c6);

        assertTrue(service.checkIfAvailable(6));

        c6.setParticipantsCount(2);
        assertFalse(service.checkIfAvailable(6));
    }

    @Test
    void testGetFulfilledCountAndCalculationStudentScore() {
        Student s = new Student("Test", "test@test.dk");

        Priority p1 = new Priority();
        p1.setPriorityNumber(1);
        p1.setFulfilled(true);

        Priority p2 = new Priority();
        p2.setPriorityNumber(2);
        p2.setFulfilled(false);

        Priority p3 = new Priority();
        p3.setPriorityNumber(3);
        p3.setFulfilled(true);

        s.setPriorityList(List.of(p1, p2, p3));

        assertEquals(2, service.getFulfilledCount(s));
        assertEquals(8, service.studentSatisfaction(s));
    }

    @Test
    void testProcessRound_FairnessMovesStudentToToBeFirst() {

        s4.setHandlingCount(0);
        Priority second = s4.getPriorityList().get(1);

        c5.setMaxParticipants(0); // FYLDT → så elev kan ikke få 1. prioritet
        c4.setMaxParticipants(1); // Kan godt få 2. prioritet

        service.getCourseCache().put(4, c4);
        service.getCourseCache().put(5, c5);

        List<Student> src = new ArrayList<>(List.of(s4));
        List<Student> fulfilled = new ArrayList<>();
        List<Student> toBeFirst = new ArrayList<>();

        service.processRoundForTest(src, fulfilled, toBeFirst, false);

        assertEquals(1, s4.getHandlingCount());
        assertTrue(second.isFulfilled());
        assertTrue(toBeFirst.contains(s4));  // fordi prioritet 1 != handlingCount 0
    }

}