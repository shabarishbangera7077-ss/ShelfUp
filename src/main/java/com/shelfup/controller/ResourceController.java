package com.shelfup.controller;

import com.shelfup.dto.ResourceDto;
import com.shelfup.entity.Resource;
import com.shelfup.entity.ResourceStatus;
import com.shelfup.entity.ResourceType;
import com.shelfup.entity.Subject;
import com.shelfup.entity.User;
import com.shelfup.repository.ResourceRepository;
import com.shelfup.repository.ResourceVoteRepository;
import com.shelfup.repository.SubjectRepository;
import com.shelfup.repository.UserRepository;
import com.shelfup.service.StorageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ResourceController {
    private final ResourceRepository resourceRepository;
    private final ResourceVoteRepository resourceVoteRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    public ResourceController(ResourceRepository resourceRepository,
                             ResourceVoteRepository resourceVoteRepository,
                             SubjectRepository subjectRepository,
                             UserRepository userRepository,
                             StorageService storageService) {
        this.resourceRepository = resourceRepository;
        this.resourceVoteRepository = resourceVoteRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    @PostMapping("/resources")
    public Resource createResource(@RequestParam String title,
                                  @RequestParam(required = false) String description,
                                  @RequestParam String type,
                                  @RequestParam Long subjectId,
                                  @RequestParam MultipartFile file) throws IOException {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String mime = file.getContentType();
        if (mime == null || !(mime.contains("pdf") || mime.contains("msword") || mime.contains("officedocument") || mime.contains("vnd.ms-powerpoint") || mime.contains("image"))) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        String storedPath = storageService.saveFile(file);
        Resource resource = new Resource();
        resource.setTitle(title);
        resource.setDescription(description);
        resource.setType(ResourceType.valueOf(type.toUpperCase()));
        resource.setFileName(file.getOriginalFilename());
        resource.setFilePath(storedPath);
        resource.setSubject(subject);
        resource.setUploadedBy(user);
        resource.setStatus(ResourceStatus.PENDING);
        return resourceRepository.save(resource);
    }

    @GetMapping("/resources")
    public List<ResourceDto> listResources(@RequestParam(required = false) Long subjectId,
                                          @RequestParam(required = false) String type,
                                          @RequestParam(required = false) String keyword) {
        List<Resource> resources = resourceRepository.findByStatus(ResourceStatus.APPROVED);
        return resources.stream()
                .filter(resource -> subjectId == null || resource.getSubject().getId().equals(subjectId))
                .filter(resource -> type == null || resource.getType().name().equalsIgnoreCase(type))
                .filter(resource -> keyword == null || keyword.isBlank() || resource.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .map(resource -> new ResourceDto(resource.getId(), resource.getTitle(), resource.getDescription(),
                        resource.getType().name(), resource.getFileName(), resource.getStatus().name(),
                        resource.getSubject().getName(), resource.getUploadedBy().getName(),
                        resource.getUpvotes(), resource.getDownloads()))
                .toList();
    }

    @GetMapping("/resources/my")
    public List<Resource> myResources() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return resourceRepository.findByUploadedBy(user);
    }

    @GetMapping("/resources/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws IOException {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
        if (resource.getStatus() != ResourceStatus.APPROVED) {
            throw new IllegalArgumentException("Resource is not approved yet");
        }
        resource.setDownloads(resource.getDownloads() + 1);
        resourceRepository.save(resource);
        Path path = Path.of(resource.getFilePath());
        byte[] bytes = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFileName() + "\"")
                .body(bytes);
    }

    @PostMapping("/resources/{id}/vote")
    public Resource toggleVote(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
        if (resource.getUploadedBy().getId().equals(user.getId())) {
            throw new SecurityException("You cannot vote on your own upload");
        }
        var vote = resourceVoteRepository.findByUserAndResource(user, resource);
        if (vote.isPresent()) {
            resourceVoteRepository.delete(vote.get());
            resource.setUpvotes(Math.max(0, resource.getUpvotes() - 1));
        } else {
            com.shelfup.entity.ResourceVote resourceVote = new com.shelfup.entity.ResourceVote();
            resourceVote.setUser(user);
            resourceVote.setResource(resource);
            resourceVoteRepository.save(resourceVote);
            resource.setUpvotes(resource.getUpvotes() + 1);
        }
        return resourceRepository.save(resource);
    }
}
