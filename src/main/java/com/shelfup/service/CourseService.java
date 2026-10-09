package com.shelfup.service;

import com.shelfup.dto.CourseDto;
import com.shelfup.entity.Course;
import com.shelfup.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(course -> new CourseDto(course.getId(), course.getName()))
                .toList();
    }

    public CourseDto createCourse(String name) {
        Course course = new Course();
        course.setName(name);
        course = courseRepository.save(course);
        return new CourseDto(course.getId(), course.getName());
    }

    public Course getCourseEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
    }
}
