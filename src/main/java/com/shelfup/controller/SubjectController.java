package com.shelfup.controller;

import com.shelfup.dto.SubjectDto;
import com.shelfup.service.SubjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping("/subjects")
    public List<SubjectDto> getSubjects(@RequestParam(required = false) Long courseId,
                                       @RequestParam(required = false) Integer semester) {
        return subjectService.getSubjects(courseId, semester);
    }

    @PostMapping("/admin/subjects")
    public SubjectDto createSubject(@RequestParam Long courseId,
                                   @RequestParam String name,
                                   @RequestParam Integer semester) {
        return subjectService.createSubject(courseId, name, semester);
    }
}
