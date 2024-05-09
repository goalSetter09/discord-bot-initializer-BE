package hongik.discordbots.initializer.controller;

import hongik.discordbots.initializer.service.FileDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
                Resource resource = fileDownloadService.createCombinedPythonFile(dependencies, programmingLanguage);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bot.py\"")
                        .contentType(MediaType.parseMediaType("text/plain"))
                        .body(resource);
            } else { // 자바랑 자바스크립트!!!!!!!!!!!!!???????????? 아직 구현 안됐다.
                /**?
                 * ?
                 */
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
