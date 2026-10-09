package com.shelfup.repository;

import com.shelfup.entity.Resource;
import com.shelfup.entity.ResourceVote;
import com.shelfup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceVoteRepository extends JpaRepository<ResourceVote, Long> {
    boolean existsByUserAndResource(User user, Resource resource);
    Optional<ResourceVote> findByUserAndResource(User user, Resource resource);
}
