package hongik.discordbots.initializer.service;

import hongik.discordbots.initializer.dto.FileDownloadResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileResourceService {

    public ByteArrayResource createCombinedPythonFile(List<String> dependencies) {
        // Load the contents of each Python file
        String header = loadFileContent("src/main/resources/static/Python/header.py");
        String footer = loadFileContent("src/main/resources/static/Python/footer.py");

        // Combine the contents based on the dependencies
        StringBuilder mainPyContent = new StringBuilder();
        mainPyContent.append(header).append("\n");

        // 이 부분은 추후에 바꿔야 할 듯 이름을 매개변수로 받는 매서드 생성해서
        if (dependencies.contains("pingpong")) {
            mainPyContent.append(loadFileContent("src/main/resources/static/Python/pingpong.py")).append("\n");
        }

        if (dependencies.contains("say")) {
            mainPyContent.append(loadFileContent("src/main/resources/static/Python/say.py")).append("\n");
        }

        mainPyContent.append(footer);

        return new ByteArrayResource(mainPyContent.toString().getBytes());
    }

    private String loadFileContent(String filePath) {
        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            if (Files.exists(path)) {
                return Files.readString(path);
            } else {
                throw new IllegalArgumentException("File not found: " + filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    public FileDownloadResponse createDependenciesZip(String programmingLanguage, List<String> dependencies) {
        // Create a zip file containing the dependencies for the specified programming language
        // This is a placeholder implementation
        byte[] data = "".getBytes();
        return new FileDownloadResponse(programmingLanguage + "-dependencies.zip", "application/zip", data);
    }
}