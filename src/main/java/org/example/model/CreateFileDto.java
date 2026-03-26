package org.example.model;

public record CreateFileDto(
        String filename,
        String base64File
) {
}
