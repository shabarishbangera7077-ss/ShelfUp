package com.shelfup.controller;

import com.shelfup.entity.Resource;
import com.shelfup.entity.ResourceRequest;
import com.shelfup.entity.RequestStatus;
import com.shelfup.entity.RequestVote;
import com.shelfup.entity.Subject;
import com.shelfup.entity.User;
import com.shelfup.repository.ResourceRepository;
import com.shelfup.repository.ResourceRequestRepository;
import com.shelfup.repository.RequestVoteRepository;
import com.shelfup.repository.SubjectRepository;
import com.shelfup.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RequestController {
    private final ResourceRequestRepository requestRepository;
    private final RequestVoteRepository requestVoteRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    public RequestController(ResourceRequestRepository requestRepository,
                            RequestVoteRepository requestVoteRepository,
                            SubjectRepository subjectRepository,
                            UserRepository userRepository,
                            ResourceRepository resourceRepository) {
        this.requestRepository = requestRepository;
        this.requestVoteRepository = requestVoteRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }

    @PostMapping("/requests")
    public ResourceRequest createRequest(@RequestParam String title,
                                        @RequestParam(required = false) String description,
                                        @RequestParam Long subjectId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));

        ResourceRequest request = new ResourceRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setSubject(subject);
        request.setRequestedBy(user);
        return requestRepository.save(request);
    }

    @GetMapping("/requests")
    public List<ResourceRequest> getRequests(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return requestRepository.findByStatus(RequestStatus.valueOf(status.toUpperCase()));
        }
        return requestRepository.findAll();
    }

    @GetMapping("/requests/my")
    public List<ResourceRequest> myRequests() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return requestRepository.findByRequestedBy(user);
    }

    @PostMapping("/requests/{id}/vote")
    public ResourceRequest toggleVote(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ResourceRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
        var vote = requestVoteRepository.findByUserAndRequest(user, request);
        if (vote.isPresent()) {
            requestVoteRepository.delete(vote.get());
            request.setUpvotes(Math.max(0, request.getUpvotes() - 1));
        } else {
            RequestVote requestVote = new RequestVote();
            requestVote.setUser(user);
            requestVote.setRequest(request);
            requestVoteRepository.save(requestVote);
            request.setUpvotes(request.getUpvotes() + 1);
        }
        return requestRepository.save(request);
    }

    @PostMapping("/requests/{id}/fulfill")
    public ResourceRequest fulfill(@PathVariable Long id, @RequestParam Long resourceId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ResourceRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
        if (resource.getStatus() != com.shelfup.entity.ResourceStatus.APPROVED) {
            throw new IllegalArgumentException("Resource must be approved");
        }
        if (!resource.getSubject().getId().equals(request.getSubject().getId())) {
            throw new IllegalArgumentException("Resource subject must match the request subject");
        }
        if (!resource.getUploadedBy().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only the resource owner can fulfill this request");
        }
        request.setStatus(RequestStatus.FULFILLED);
        request.setFulfilledByResource(resource);
        return requestRepository.save(request);
    }
}
