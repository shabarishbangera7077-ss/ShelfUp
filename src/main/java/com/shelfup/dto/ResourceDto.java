package com.shelfup.dto;

public record ResourceDto(Long id, String title, String description, String type, String fileName, String status,
                        String subjectName, String uploaderName, long upvotes, long downloads) {}
