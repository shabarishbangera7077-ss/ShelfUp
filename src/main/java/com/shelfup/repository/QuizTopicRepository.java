package com.shelfup.repository;

import com.shelfup.entity.QuizTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizTopicRepository extends JpaRepository<QuizTopic, Long> {
}
