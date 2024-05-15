package hongik.discordbots.initializer.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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
public class FileDownloadService {

    public Resource createDependenciesZip(String programmingLanguage, List<String> dependencies) throws IOException {
        if (dependencies.isEmpty()) {
            throw new IllegalArgumentException("No dependencies provided");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (String dependency : dependencies) {
                String fileName = dependency + ".zip";
                Path file = Paths.get("src/main/resources/static/" + programmingLanguage + "/" + fileName).toAbsolutePath().normalize();

                if (Files.exists(file)) {
                    zos.putNextEntry(new ZipEntry(file.getFileName().toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                }
            }
            zos.finish();
        }
        return new ByteArrayResource(baos.toByteArray());
    }


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
