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
public class JavaFileService {

    public FileDownloadResponse createJavaBotZip(List<String> dependencies) {
        try {
            // 여기서 실질적인 자바 프로젝트 파일 생성
            ByteArrayResource mainJavaResource = createJavaProjectFile(dependencies);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                zos.putNextEntry(new ZipEntry("Main.java")); // 자바의 주 실행 파일 이름으로 가정
                zos.write(mainJavaResource.getByteArray());
                zos.closeEntry();


                // 이 부분 하나의 클래스로 선언해서 불러오는 식으로 변경해야 할 듯
                String[] additionalFiles = {"install_requirements.bat", "install_requirements.sh", "requirements.txt", "settings.java"};
                String basePath = "src/main/resources/static/index/";

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
            return new FileDownloadResponse("java_environment.zip", "application/zip", baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error creating Java environment ZIP", e);
        }
    }

    private ByteArrayResource createJavaProjectFile(List<String> dependencies) {
        // Load the contents of the project's base Java files
        String header = loadFileContent("src/main/resources/static/Java/header.java"); // 예시 파일
        String footer = loadFileContent("src/main/resources/static/Java/footer.java");

        // Initialize the main Java content with the header
        StringBuilder mainJavaContent = new StringBuilder();
        mainJavaContent.append(header).append("\n");

        // Dynamically append the content of each dependency file
        for (String dependency : dependencies) {
            String filePath = "src/main/resources/static/Java/" + dependency + ".java";
            mainJavaContent.append(loadFileContent(filePath)).append("\n");
        }

        // Append the footer at the end
        mainJavaContent.append(footer);

        return new ByteArrayResource(mainJavaContent.toString().getBytes());
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
}
