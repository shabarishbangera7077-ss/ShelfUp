package com.shelfup.repository;

import com.shelfup.entity.Resource;
import com.shelfup.entity.ResourceStatus;
import com.shelfup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByStatus(ResourceStatus status);
    List<Resource> findByUploadedBy(User user);
    List<Resource> findByStatusAndSubjectId(ResourceStatus status, Long subjectId);
}
