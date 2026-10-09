package com.shelfup.repository;

import com.shelfup.entity.Question;
import com.shelfup.entity.QuizDifficulty;
import com.shelfup.entity.QuizTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByApprovedTrue();
    List<Question> findByApprovedTrueAndTopicAndDifficulty(QuizTopic topic, QuizDifficulty difficulty);
    long countByApprovedTrue();
}
