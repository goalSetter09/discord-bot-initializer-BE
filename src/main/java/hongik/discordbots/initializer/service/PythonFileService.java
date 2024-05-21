package hongik.discordbots.initializer.service;

import hongik.discordbots.initializer.dto.FileDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class PythonFileService {

    private final FileResourceService fileResourceService;

    // 실행파일 + 봇파일 등 포함해서 zip으로 만들어주는 매서드
    public FileDownloadResponse createPythonBotZip(List<String> dependencies) {
        try {

            // 여기서 실질적인 파이썬 봇 파일 생성
            ByteArrayResource mainPyResource = fileResourceService.createCombinedPythonFile(dependencies);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                zos.putNextEntry(new ZipEntry("main.py"));
                zos.write(mainPyResource.getByteArray());
                zos.closeEntry();

                // 이 부분 하나의 클래스로 선언해서 불러오는 식으로 변경해야 할 듯
                String[] additionalFiles = {"install_requirements.bat", "install_requirements.sh", "requirements.txt", "settings.py"};
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
            return new FileDownloadResponse("python_environment.zip", "application/zip", baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error creating Python environment ZIP", e);
        }
    }
}
