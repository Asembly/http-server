package asembly.httpserver.model;

public record CreateFileDto(
        String filename,
        String base64File
) {
}
