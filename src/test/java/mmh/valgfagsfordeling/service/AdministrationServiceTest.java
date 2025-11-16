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
    Course c1;
    Course c2;
    Course c3;


    @BeforeEach
    void setUp() {
        service = new AdministrationService(studentRepo, courseRepo, priorityRepo, teacherRepo);
        service.setRandom(new Random(1));
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

        c1 = course(1, "C1");
        c2 = course(2, "C2");
        c3 = course(3, "C3");

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
    }

    @Test
    void testDistributionBasicScenario() {

        // arrange
        setUpTestData();

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















}