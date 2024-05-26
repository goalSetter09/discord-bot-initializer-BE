package hongik.discordbots.initializer.service;

import hongik.discordbots.initializer.dto.FileDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FileDownloadService {

    // 이렇게 실행되는 서비스를 추후에 interface를 구현하는 식으로 변경할 예정(예: FileService 라는 인터페이스를 PythonFileService가 구현)
    private final PythonFileService pythonFileService;
    private final JavaFileService javaFileService;
    public FileDownloadResponse generateDownloadFile(String programmingLanguage, List<String> dependencies) throws IOException{
        if (programmingLanguage.equals("Python")) {
            return pythonFileService.createPythonBotZip(dependencies);
        }
        else if(programmingLanguage.equals("Java")){
            return javaFileService.createJavaBotZip(dependencies);
        }
        else {
            // 이 부분 아직 실행되지 않음
            return pythonFileService.createDependenciesZip(programmingLanguage, dependencies);
        }
    }

    // 이 매서드는 아직 실행되지 않음
    public Resource createCombinedPythonFile(List<String> dependencies, String programmingLanguage) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Add the contents of the header.py file first
        Path headerPath = Paths.get("src/main/resources/static/", programmingLanguage, "header.py").toAbsolutePath().normalize();
        if (Files.exists(headerPath)) {
            byte[] headerBytes = Files.readAllBytes(headerPath);
            outputStream.write(headerBytes);
            outputStream.write("\n".getBytes());  // Add a newline to separate the header from the following content
        }

        // Iterate over the dependency files and add their contents
        for (String dependency : dependencies) {
            Path filePath = Paths.get("src/main/resources/static/", programmingLanguage, dependency + ".py").toAbsolutePath().normalize();
            if (Files.exists(filePath)) {
                byte[] fileBytes = Files.readAllBytes(filePath);
                outputStream.write(fileBytes);
                outputStream.write("\n".getBytes());  // Add a newline to separate each file's content
            }
        }

        // Add the contents of the footer.py file last
        Path footerPath = Paths.get("src/main/resources/static/", programmingLanguage, "footer.py").toAbsolutePath().normalize();
        if (Files.exists(footerPath)) {
            byte[] footerBytes = Files.readAllBytes(footerPath);
            outputStream.write(footerBytes);
            outputStream.write("\n".getBytes());  // Add a newline to separate the footer from the previous content
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }

}
