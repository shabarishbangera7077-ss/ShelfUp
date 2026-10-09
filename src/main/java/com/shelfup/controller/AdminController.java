package com.shelfup.controller;

import com.shelfup.entity.Resource;
import com.shelfup.entity.ResourceStatus;
import com.shelfup.entity.User;
import com.shelfup.repository.ResourceRepository;
import com.shelfup.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public AdminController(ResourceRepository resourceRepository, UserRepository userRepository) {
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/resources")
    public List<Resource> getResources(@RequestParam(required = false) String status) {
        if (status == null || status.isBlank()) {
            return resourceRepository.findAll();
        }
        return resourceRepository.findByStatus(ResourceStatus.valueOf(status.toUpperCase()));
    }

    @PatchMapping("/resources/{id}/approve")
    public Resource approve(@PathVariable Long id) {
        Resource resource = getResource(id);
        resource.setStatus(ResourceStatus.APPROVED);
        return resourceRepository.save(resource);
    }

    @PatchMapping("/resources/{id}/reject")
    public Resource reject(@PathVariable Long id) {
        Resource resource = getResource(id);
        resource.setStatus(ResourceStatus.REJECTED);
        return resourceRepository.save(resource);
    }

    @DeleteMapping("/resources/{id}")
    public void deleteResource(@PathVariable Long id) throws Exception {
        Resource resource = getResource(id);
        Path path = Path.of(resource.getFilePath());
        if (Files.exists(path)) {
            Files.delete(path);
        }
        resourceRepository.delete(resource);
    }

    @PatchMapping("/users/{id}/block")
    public User blockUser(@PathVariable Long id) {
        User user = getUser(id);
        user.setBlocked(true);
        return userRepository.save(user);
    }

    @PatchMapping("/users/{id}/unblock")
    public User unblockUser(@PathVariable Long id) {
        User user = getUser(id);
        user.setBlocked(false);
        return userRepository.save(user);
    }

    private Resource getResource(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
