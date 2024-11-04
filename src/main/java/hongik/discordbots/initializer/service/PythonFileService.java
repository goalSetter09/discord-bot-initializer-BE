package hongik.discordbots.initializer.service;

import hongik.discordbots.initializer.dto.FileDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class PythonFileService {

    private static final String fileBasePath = "src/main/resources/static/index/";
    private static final String pythonBasePath = "src/main/resources/static/Python/";
    private static String[] additionalFiles = {"install_requirements.bat", "install_requirements.sh", "requirements.txt", "settings.py"};

    // 실행파일 + 봇파일 등 포함해서 zip으로 만들어주는 매서드
    public FileDownloadResponse createPythonBotZip(List<String> dependencies) {
        try {

            // 여기서 실질적인 파이썬 봇 파일 생성
            ByteArrayResource mainPyResource = createPythonBotFile(dependencies);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                zos.putNextEntry(new ZipEntry("main.py"));
                zos.write(mainPyResource.getByteArray());
                zos.closeEntry();

                // 이 부분 하나의 클래스로 선언해서 불러오는 식으로 변경해야 할 듯

                for (String fileName : additionalFiles) {
                    Path filePath = Paths.get(fileBasePath, fileName).toAbsolutePath().normalize();
                    if (Files.exists(filePath)) {
                        zos.putNextEntry(new ZipEntry(fileName));
                        Files.copy(filePath, zos); // 파일의 모든 내용을 output stream으로 복사
                        zos.closeEntry();
                    }
                }
                zos.finish();
            }
            return new FileDownloadResponse("python_environment.zip", "application/zip", baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error creating Python environment ZIP", e);
        }
    }

    // 원하는 기능을 가진 파이썬 봇 파일 생성하는 매서드
    private ByteArrayResource createPythonBotFile(List<String> dependencies) {
        // Load the contents of the header and footer Python files
        String header = loadFileContent(pythonBasePath + "header.py");
        String footer = loadFileContent(pythonBasePath + "footer.py");

        // Initialize the main Python content with the header
        StringBuilder mainPyContent = new StringBuilder();
        mainPyContent.append(header).append("\n");

        // 매개변수로 수정
        // Dynamically append the content of each dependency file
        for (String dependency : dependencies) {
            String filePath = pythonBasePath + dependency + ".py";
            mainPyContent.append(loadFileContent(filePath)).append("\n");
        }

        // Append the footer at the end
        mainPyContent.append(footer);

        return new ByteArrayResource(mainPyContent.toString().getBytes());
    }

    // 봇 파일 내부에 추가할 내용 불러오는 매서드
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


    // 이 매서드는 아직 실행되지 않음
    public FileDownloadResponse createDependenciesZip(String programmingLanguage, List<String> dependencies) {
        // Create a zip file containing the dependencies for the specified programming language
        // This is a placeholder implementation
        byte[] data = "".getBytes();
        return new FileDownloadResponse(programmingLanguage + "-dependencies.zip", "application/zip", data);
    }
}
