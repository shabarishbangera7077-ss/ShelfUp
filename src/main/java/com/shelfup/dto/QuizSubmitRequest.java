package com.shelfup.dto;

import java.util.List;

public record QuizSubmitRequest(Long attemptId, List<AttemptAnswerDto> answers, Integer timeTaken) {}
