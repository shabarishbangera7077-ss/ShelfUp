package com.shelfup.controller;

import com.shelfup.dto.AttemptAnswerDto;
import com.shelfup.dto.QuizSubmitRequest;
import com.shelfup.dto.QuizTopicDto;
import com.shelfup.entity.QuizAttempt;
import com.shelfup.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/topics")
    public List<QuizTopicDto> getTopics() {
        return quizService.getTopics();
    }

    @PostMapping("/start")
    public QuizAttempt startQuiz(@RequestParam Long topicId,
                                @RequestParam String difficulty,
                                @RequestParam String mode) {
        return quizService.startQuiz(topicId, difficulty, mode);
    }

    @PostMapping("/submit")
    public QuizAttempt submitQuiz(@Valid @RequestBody QuizSubmitRequest request) {
        return quizService.submitQuiz(request.attemptId(), request.answers(), request.timeTaken());
    }
}
