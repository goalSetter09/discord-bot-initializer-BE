package hongik.discordbots.initializer.service;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileDownloadService {

    public ByteArrayResource createDependenciesZip(String programmingLanguage, List<String> dependencies) throws Exception {
        if (dependencies.isEmpty()) {
            throw new IllegalArgumentException("No dependencies provided");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (String dependency : dependencies) {
                String fileName = URLEncoder.encode(dependency + ".zip", StandardCharsets.UTF_8);
                Path file = Paths.get("src/main/resources/static/" + programmingLanguage + "/" + fileName)
                        .toAbsolutePath().normalize();

                if (Files.exists(file)) {
                    zos.putNextEntry(new ZipEntry(file.getFileName().toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                }
            }

            zos.finish();
            return new ByteArrayResource(baos.toByteArray());
        }
    }
}
