package com.shelfup.dto;

public record ResourceRequestDto(Long id, String title, String description, String subjectName,
                                String requestedBy, String status, long upvotes) {}
