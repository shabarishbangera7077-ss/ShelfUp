package com.shelfup.repository;

import com.shelfup.entity.QuizAttempt;
import com.shelfup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUser(User user);
}
