package com.shelfup.service;

import com.shelfup.dto.AttemptAnswerDto;
import com.shelfup.dto.QuizTopicDto;
import com.shelfup.entity.AttemptAnswer;
import com.shelfup.entity.Badge;
import com.shelfup.entity.Question;
import com.shelfup.entity.QuizAttempt;
import com.shelfup.entity.QuizAttemptStatus;
import com.shelfup.entity.QuizDifficulty;
import com.shelfup.entity.QuizMode;
import com.shelfup.entity.QuizTopic;
import com.shelfup.entity.User;
import com.shelfup.entity.UserBadge;
import com.shelfup.entity.UserStats;
import com.shelfup.repository.AttemptAnswerRepository;
import com.shelfup.repository.BadgeRepository;
import com.shelfup.repository.QuestionRepository;
import com.shelfup.repository.QuizAttemptRepository;
import com.shelfup.repository.QuizTopicRepository;
import com.shelfup.repository.UserBadgeRepository;
import com.shelfup.repository.UserRepository;
import com.shelfup.repository.UserStatsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class QuizService {
    private final QuizTopicRepository quizTopicRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final UserStatsRepository userStatsRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public QuizService(QuizTopicRepository quizTopicRepository,
                       QuestionRepository questionRepository,
                       QuizAttemptRepository quizAttemptRepository,
                       UserRepository userRepository,
                       AttemptAnswerRepository attemptAnswerRepository,
                       UserStatsRepository userStatsRepository,
                       BadgeRepository badgeRepository,
                       UserBadgeRepository userBadgeRepository) {
        this.quizTopicRepository = quizTopicRepository;
        this.questionRepository = questionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
        this.attemptAnswerRepository = attemptAnswerRepository;
        this.userStatsRepository = userStatsRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    public List<QuizTopicDto> getTopics() {
        return quizTopicRepository.findAll().stream()
                .map(topic -> new QuizTopicDto(topic.getId(), topic.getName()))
                .toList();
    }

    public QuizAttempt startQuiz(Long topicId, String difficulty, String mode) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        QuizTopic topic = quizTopicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        List<Question> questions = questionRepository.findByApprovedTrueAndTopicAndDifficulty(topic, QuizDifficulty.valueOf(difficulty.toUpperCase()));
        if (questions.size() < 10) {
            throw new IllegalArgumentException("Not enough approved questions for the selected topic and difficulty");
        }
        Collections.shuffle(questions, new Random());
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setTopic(topic);
        attempt.setMode(QuizMode.valueOf(mode.toUpperCase()));
        attempt.setTotalQuestions(10);
        attempt.setScore(0);
        attempt.setTimeTaken(0);
        return quizAttemptRepository.save(attempt);
    }

    public QuizAttempt submitQuiz(Long attemptId, List<AttemptAnswerDto> answers, Integer timeTaken) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz attempt not found"));
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You cannot submit someone else's quiz");
        }
        if (attempt.getStatus() != QuizAttemptStatus.STARTED) {
            throw new IllegalArgumentException("Quiz already submitted");
        }

        int score = 0;
        List<AttemptAnswer> savedAnswers = new ArrayList<>();
        for (AttemptAnswerDto answer : answers) {
            Question question = questionRepository.findById(answer.questionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question not found"));
            boolean correct = question.getCorrectOption().equalsIgnoreCase(answer.selectedOption());
            if (correct) {
                score += 10;
            }
            AttemptAnswer attemptAnswer = new AttemptAnswer();
            attemptAnswer.setAttempt(attempt);
            attemptAnswer.setQuestion(question);
            attemptAnswer.setSelectedOption(answer.selectedOption() == null ? "" : answer.selectedOption());
            attemptAnswer.setCorrect(correct);
            savedAnswers.add(attemptAnswerRepository.save(attemptAnswer));
        }

        attempt.setScore(score);
        attempt.setTimeTaken(timeTaken == null ? 0 : timeTaken);
        attempt.setStatus(QuizAttemptStatus.SUBMITTED);
        quizAttemptRepository.save(attempt);

        UserStats userStats = userStatsRepository.findByUser(user)
                .orElseGet(() -> {
                    UserStats stats = new UserStats();
                    stats.setUser(user);
                    return stats;
                });
        userStats.setXp(userStats.getXp() + score);
        userStats.setLevel(calculateLevel(userStats.getXp()));
        userStats.setLastPlayedDate(LocalDate.now());
        userStatsRepository.save(userStats);

        awardBadgesIfNeeded(user, userStats);
        return attempt;
    }

    private int calculateLevel(int xp) {
        if (xp < 200) return 1;
        if (xp < 600) return 2;
        if (xp < 1200) return 3;
        return 4;
    }

    private void awardBadgesIfNeeded(User user, UserStats stats) {
        List<UserBadge> existing = userBadgeRepository.findByUser(user);
        if (stats.getXp() >= 200 && existing.stream().noneMatch(b -> b.getBadge().getName().equals("Beginner"))) {
            awardBadge(user, "Beginner", "Reached 200 XP");
        }
        if (stats.getXp() >= 600 && existing.stream().noneMatch(b -> b.getBadge().getName().equals("Intermediate"))) {
            awardBadge(user, "Intermediate", "Reached 600 XP");
        }
    }

    private void awardBadge(User user, String name, String description) {
        Badge badge = badgeRepository.findByName(name);
        if (badge == null) {
            badge = new Badge();
            badge.setName(name);
            badge.setDescription(description);
            badge = badgeRepository.save(badge);
        }
        boolean alreadyAwarded = userBadgeRepository.findByUser(user).stream()
                .anyMatch(entry -> entry.getBadge().getName().equals(name));
        if (!alreadyAwarded) {
            UserBadge userBadge = new UserBadge();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadgeRepository.save(userBadge);
        }
    }
}
