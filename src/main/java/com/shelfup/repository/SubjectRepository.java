package com.shelfup.repository;

import com.shelfup.entity.Course;
import com.shelfup.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByCourseAndSemester(Course course, Integer semester);
    List<Subject> findByCourseId(Long courseId);
}
