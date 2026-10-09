package com.shelfup.service;

import com.shelfup.dto.SubjectDto;
import com.shelfup.entity.Course;
import com.shelfup.entity.Subject;
import com.shelfup.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final CourseService courseService;

    public SubjectService(SubjectRepository subjectRepository, CourseService courseService) {
        this.subjectRepository = subjectRepository;
        this.courseService = courseService;
    }

    public List<SubjectDto> getSubjects(Long courseId, Integer semester) {
        if (courseId != null && semester != null) {
            Course course = courseService.getCourseEntity(courseId);
            return subjectRepository.findByCourseAndSemester(course, semester).stream()
                    .map(subject -> new SubjectDto(subject.getId(), subject.getName(), subject.getCourse().getId(),
                            subject.getCourse().getName(), subject.getSemester()))
                    .toList();
        }
        if (courseId != null) {
            return subjectRepository.findByCourseId(courseId).stream()
                    .map(subject -> new SubjectDto(subject.getId(), subject.getName(), subject.getCourse().getId(),
                            subject.getCourse().getName(), subject.getSemester()))
                    .toList();
        }
        return subjectRepository.findAll().stream()
                .map(subject -> new SubjectDto(subject.getId(), subject.getName(), subject.getCourse().getId(),
                        subject.getCourse().getName(), subject.getSemester()))
                .toList();
    }

    public SubjectDto createSubject(Long courseId, String name, Integer semester) {
        Course course = courseService.getCourseEntity(courseId);
        Subject subject = new Subject();
        subject.setCourse(course);
        subject.setName(name);
        subject.setSemester(semester);
        subject = subjectRepository.save(subject);
        return new SubjectDto(subject.getId(), subject.getName(), course.getId(), course.getName(), subject.getSemester());
    }

    public Subject getSubjectEntity(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
    }
}
