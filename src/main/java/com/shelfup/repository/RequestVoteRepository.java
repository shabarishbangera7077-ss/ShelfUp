package com.shelfup.repository;

import com.shelfup.entity.RequestVote;
import com.shelfup.entity.ResourceRequest;
import com.shelfup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestVoteRepository extends JpaRepository<RequestVote, Long> {
    boolean existsByUserAndRequest(User user, ResourceRequest request);
    Optional<RequestVote> findByUserAndRequest(User user, ResourceRequest request);
}
