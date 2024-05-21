package hongik.discordbots.initializer.controller;

import hongik.discordbots.initializer.dto.FileDownloadResponse;
import hongik.discordbots.initializer.service.FileDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileDownloadController {

    private final FileDownloadService fileDownloadService;

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam("programmingLanguage") String programmingLanguage,
            @RequestParam("dependencies") List<String> dependencies) throws IOException {
        FileDownloadResponse response = fileDownloadService.generateDownloadFile(programmingLanguage, dependencies);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .body(response.getFileData());
    }
}
