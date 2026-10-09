package com.shelfup.repository;

import com.shelfup.entity.ResourceRequest;
import com.shelfup.entity.RequestStatus;
import com.shelfup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRequestRepository extends JpaRepository<ResourceRequest, Long> {
    List<ResourceRequest> findByStatus(RequestStatus status);
    List<ResourceRequest> findByRequestedBy(User user);
}
