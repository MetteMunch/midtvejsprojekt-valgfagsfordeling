package mmh.valgfagsfordeling.service;

import mmh.valgfagsfordeling.dto.StudentDTO;
import mmh.valgfagsfordeling.model.Priority;
import mmh.valgfagsfordeling.model.Student;
import org.springframework.stereotype.Service;

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
    private List<Student> toBeFirstList2;
    private List<Student> fulfilled2;
    private List<Student> toBeManualHandled;

    public AdministrationService(StudentService studentService, CourseService courseService, PriorityService priorityService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.priorityService = priorityService;
    }

    public void distributionGreedyWithFainess() {

        if (!initListAllStudents.isEmpty()) {
            Student selectedStudent = getRandomStudent(initListAllStudents);
            int handlingCount = selectedStudent.getHandlingCount();
            Priority priorityToBeChecked = selectedStudent.getPriorityList().get(handlingCount);
            if (courseService.checkIfAvailable(priorityToBeChecked.getCourse().getCourseId())) {
                priorityToBeChecked.setFulfilled(true);
                fulfilled1.add(selectedStudent);
                selectedStudent.setHandlingCount();
                courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
            } else {
                Priority priorityToBeCheckedSecond = selectedStudent.getPriorityList().get(handlingCount+1);
                if (courseService.checkIfAvailable(priorityToBeCheckedSecond.getCourse().getCourseId())) {
                    priorityToBeCheckedSecond.setFulfilled(true);
                    toBeFirstList1.add(selectedStudent);
                    selectedStudent.setHandlingCount();
                    courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
                } else {
                    Priority priorityToBeCheckedThird = selectedStudent.getPriorityList().get(handlingCount+2);
                    if (courseService.checkIfAvailable(priorityToBeCheckedThird.getCourse().getCourseId())) {
                        priorityToBeCheckedThird.setFulfilled(true);
                        toBeFirstList1.add(selectedStudent);
                        selectedStudent.setHandlingCount();
                        courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
                    }
                }
            }
        }
        if (!toBeFirstList1.isEmpty()) {
            Student selectedStudent = getRandomStudent(toBeFirstList1);
            int handlingCount = selectedStudent.getHandlingCount();
            Priority priorityToBeChecked = selectedStudent.getPriorityList().get(handlingCount);
            if (courseService.checkIfAvailable(priorityToBeChecked.getCourse().getCourseId())) {
                priorityToBeChecked.setFulfilled(true);
                fulfilled2.add(selectedStudent);
                selectedStudent.setHandlingCount();
                courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
            } else {
                Priority priorityToBeCheckedSecond = selectedStudent.getPriorityList().get(handlingCount+1);
                if (courseService.checkIfAvailable(priorityToBeCheckedSecond.getCourse().getCourseId())) {
                    priorityToBeCheckedSecond.setFulfilled(true);
                    toBeFirstList2.add(selectedStudent);
                    selectedStudent.setHandlingCount();
                    courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
                } else {
                    Priority priorityToBeCheckedThird = selectedStudent.getPriorityList().get(handlingCount+2);
                    if (courseService.checkIfAvailable(priorityToBeCheckedThird.getCourse().getCourseId())) {
                        priorityToBeCheckedThird.setFulfilled(true);
                        toBeFirstList2.add(selectedStudent);
                        selectedStudent.setHandlingCount();
                        courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
                    }
                }
            }
        }
        if (!fulfilled1.isEmpty()) {
            Student selectedStudent = getRandomStudent(fulfilled1);
            int handlingCount = selectedStudent.getHandlingCount();
            Priority priorityToBeChecked = selectedStudent.getPriorityList().get(handlingCount);
            if (courseService.checkIfAvailable(priorityToBeChecked.getCourse().getCourseId())) {
                priorityToBeChecked.setFulfilled(true);
                fulfilled2.add(selectedStudent);
                selectedStudent.setHandlingCount();
                courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
            } else {
                Priority priorityToBeCheckedSecond = selectedStudent.getPriorityList().get(handlingCount+1);
                if (courseService.checkIfAvailable(priorityToBeCheckedSecond.getCourse().getCourseId())) {
                    priorityToBeCheckedSecond.setFulfilled(true);
                    toBeFirstList2.add(selectedStudent);
                    selectedStudent.setHandlingCount();
                    courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
                } else {
                    Priority priorityToBeCheckedThird = selectedStudent.getPriorityList().get(handlingCount+2);
                    if (courseService.checkIfAvailable(priorityToBeCheckedThird.getCourse().getCourseId())) {
                        priorityToBeCheckedThird.setFulfilled(true);
                        toBeFirstList2.add(selectedStudent);
                        selectedStudent.setHandlingCount();
                        courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
                    }
                }
            }
        }
        if (!toBeFirstList2.isEmpty()) {
            Student selectedStudent = getRandomStudent(toBeFirstList2);
            int handlingCount = selectedStudent.getHandlingCount();
            Priority priorityToBeChecked = selectedStudent.getPriorityList().get(handlingCount);
            if (courseService.checkIfAvailable(priorityToBeChecked.getCourse().getCourseId())) {
                priorityToBeChecked.setFulfilled(true);
                selectedStudent.setHandlingCount();
                courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
            } else {
                Priority priorityToBeCheckedSecond = selectedStudent.getPriorityList().get(handlingCount+1);
                if (courseService.checkIfAvailable(priorityToBeCheckedSecond.getCourse().getCourseId())) {
                    priorityToBeCheckedSecond.setFulfilled(true);
                    selectedStudent.setHandlingCount();
                    courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
                } else {
                    Priority priorityToBeCheckedThird = selectedStudent.getPriorityList().get(handlingCount+2);
                    if (courseService.checkIfAvailable(priorityToBeCheckedThird.getCourse().getCourseId())) {
                        priorityToBeCheckedThird.setFulfilled(true);
                        selectedStudent.setHandlingCount();
                        courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
                    } else {
                        toBeManualHandled.add(selectedStudent);
                    }

                }
            }
        }
        if (!fulfilled2.isEmpty()) {
            Student selectedStudent = getRandomStudent(fulfilled2);
            int handlingCount = selectedStudent.getHandlingCount();
            Priority priorityToBeChecked = selectedStudent.getPriorityList().get(handlingCount);
            if (courseService.checkIfAvailable(priorityToBeChecked.getCourse().getCourseId())) {
                priorityToBeChecked.setFulfilled(true);
                selectedStudent.setHandlingCount();
                courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
            } else {
                Priority priorityToBeCheckedSecond = selectedStudent.getPriorityList().get(handlingCount+1);
                if (courseService.checkIfAvailable(priorityToBeCheckedSecond.getCourse().getCourseId())) {
                    priorityToBeCheckedSecond.setFulfilled(true);
                    selectedStudent.setHandlingCount();
                    courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
                } else {
                    Priority priorityToBeCheckedThird = selectedStudent.getPriorityList().get(handlingCount+2);
                    if (courseService.checkIfAvailable(priorityToBeCheckedThird.getCourse().getCourseId())) {
                        priorityToBeCheckedThird.setFulfilled(true);
                        selectedStudent.setHandlingCount();
                        courseService.addCount(priorityToBeChecked.getCourse().getCourseId());
                    } else {
                        toBeManualHandled.add(selectedStudent);
                    }
                }
            }
        }

    }

    //--------------------DTO-----------------------

    public List<StudentDTO> toBeManualHandledListDTO() {
        return toBeManualHandled.stream()
                .map(student -> studentService.convertToDTO(student))
                .toList();
    }

    //--------------------Hjælpemetoder--------------





    public Student getRandomStudent(List<Student> list) {
        Random random = new Random();
        int randomIndeks = random.nextInt(0, list.size());
        Student selectedStudent = list.get(randomIndeks);
        list.remove(randomIndeks); //her fjernes eleven fra listen
        return selectedStudent;
    }

}
