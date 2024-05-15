package hongik.discordbots.initializer.controller;

import hongik.discordbots.initializer.service.FileDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequiredArgsConstructor
public class FileDownloadController {

    private final FileDownloadService fileDownloadService;

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam("programmingLanguage") String programmingLanguage,
            @RequestParam("dependencies") List<String> dependencies) {
        try {
            if ("Python".equals(programmingLanguage)) {
                // Step 1: Generate main.py
                Resource mainPyResource = fileDownloadService.createCombinedPythonFile(dependencies, programmingLanguage);

                // Step 2: Create a ByteArrayOutputStream for the ZIP file
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                    // Add main.py to the zip
                    zos.putNextEntry(new ZipEntry("main.py"));
                    zos.write(((ByteArrayResource) mainPyResource).getByteArray());
                    zos.closeEntry();

                    // Directory containing additional files. We require this for starting.
                    String[] additionalFiles = {"install_requirements.bat", "install_requirements.sh", "requirements.txt", "settings.py"};
                    String basePath = "src/main/resources/static/index/";

                    // Add each additional file to the zip
                    for (String fileName : additionalFiles) {
                        Path filePath = Paths.get(basePath, fileName).toAbsolutePath().normalize();
                        if (Files.exists(filePath)) {
                            zos.putNextEntry(new ZipEntry(fileName));
                            Files.copy(filePath, zos);
                            zos.closeEntry();
                        }
                    }
                    zos.finish();
                }

                // Step 3: Return the zip file
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"python_environment.zip\"")
                        .contentType(MediaType.parseMediaType("application/zip"))
                        .body(new ByteArrayResource(baos.toByteArray()));
            } else {
                // Handling for other languages
                Resource resource = fileDownloadService.createDependenciesZip(programmingLanguage, dependencies);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + programmingLanguage + "-dependencies.zip\"")
                        .contentType(MediaType.parseMediaType("application/zip"))
                        .body(resource);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
