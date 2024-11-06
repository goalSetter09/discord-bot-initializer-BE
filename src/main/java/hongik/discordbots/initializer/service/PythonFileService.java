package hongik.discordbots.initializer.service;

import hongik.discordbots.initializer.dto.FileDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class PythonFileService {

    private static final String fileBasePath = "static/index/";
    private static final String pythonBasePath = "static/Python/";
    private static final String[] additionalFiles = {"install_requirements.bat", "install_requirements.sh", "requirements.txt", "settings.py"};

    // 실행 파일 + 봇 파일 등 포함해서 zip으로 만들어주는 메서드
    public FileDownloadResponse createPythonBotZip(List<String> dependencies) {
        try {
            // 여기서 실질적인 파이썬 봇 파일 생성
            ByteArrayResource mainPyResource = createPythonBotFile(dependencies);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                // main.py 파일 추가
                zos.putNextEntry(new ZipEntry("main.py"));
                zos.write(mainPyResource.getByteArray());
                zos.closeEntry();

                // 추가 파일들을 ZIP에 포함
                for (String fileName : additionalFiles) {
                    ClassPathResource resource = new ClassPathResource(fileBasePath + fileName);
                    if (resource.exists()) {
                        zos.putNextEntry(new ZipEntry(fileName));
                        try (InputStream inputStream = resource.getInputStream()) {
                            inputStream.transferTo(zos); // InputStream을 ZipOutputStream으로 복사
                        }
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

    // 원하는 기능을 가진 파이썬 봇 파일 생성하는 메서드
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

    // 봇 파일 내부에 추가할 내용 불러오는 메서드
    private String loadFileContent(String filePath) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            try (InputStream inputStream = resource.getInputStream()) {
                return new String(inputStream.readAllBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    // 이 메서드는 아직 실행되지 않음
    public FileDownloadResponse createDependenciesZip(String programmingLanguage, List<String> dependencies) {
        // Create a zip file containing the dependencies for the specified programming language
        // This is a placeholder implementation
        byte[] data = "".getBytes();
        return new FileDownloadResponse(programmingLanguage + "-dependencies.zip", "application/zip", data);
    }
}
