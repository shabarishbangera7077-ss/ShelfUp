package com.shelfup.controller;

import com.shelfup.dto.CourseDto;
import com.shelfup.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public List<CourseDto> getCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping("/admin/courses")
    public CourseDto createCourse(@RequestParam String name) {
        return courseService.createCourse(name);
    }
}
