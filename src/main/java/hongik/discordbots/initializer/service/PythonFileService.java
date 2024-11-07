package hongik.discordbots.initializer.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import hongik.discordbots.initializer.dto.FileDownloadResponse;
import hongik.discordbots.initializer.s3.S3FileService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PythonFileService {

	private static final String fileBasePath = "static/index/";
	private static final String pythonBasePath = "static/Python/";
	private static final String[] additionalFiles = {"install_requirements.bat", "install_requirements.sh",
		"requirements.txt", "settings.py"};
	private final S3FileService s3FileService;

	// Combined Python ZIP 파일 생성
	public FileDownloadResponse createCombinedPythonZip(List<String> s3Paths) throws IOException {
		ByteArrayResource mainPyResource = createCombinedPythonBotFile(s3Paths);
		byte[] zipData = createZipWithAdditionalFiles(mainPyResource);

		return new FileDownloadResponse("python_combined_environment.zip", "application/zip", zipData);
	}

	// main.py 파일 생성 (header, S3 내용, footer 포함)
	private ByteArrayResource createCombinedPythonBotFile(List<String> s3Paths) throws IOException {
		String header = loadFileContent(pythonBasePath + "header.py");
		String footer = loadFileContent(pythonBasePath + "footer.py");

		StringBuilder mainPyContent = new StringBuilder();
		mainPyContent.append(header).append("\n");

		for (String s3Path : s3Paths) {
			String botContent = s3FileService.getFileContent(s3Path);
			mainPyContent.append(botContent).append("\n");
		}

		mainPyContent.append(footer);
		return new ByteArrayResource(mainPyContent.toString().getBytes());
	}

	// ZIP 파일 생성 및 추가 파일 포함
	private byte[] createZipWithAdditionalFiles(ByteArrayResource mainPyResource) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		addFileToZip(zos, mainPyResource, "main.py");

		for (String fileName : additionalFiles) {
			ClassPathResource resource = new ClassPathResource(fileBasePath + fileName);
			if (resource.exists()) {
				addFileToZip(zos, resource, fileName);
			}
		}

		zos.finish();
		return baos.toByteArray();
	}

	// 단일 파일을 ZIP에 추가하는 메서드
	private void addFileToZip(ZipOutputStream zos, ClassPathResource resource, String fileName) throws IOException {
		InputStream inputStream = resource.getInputStream();
		zos.putNextEntry(new ZipEntry(fileName));
		inputStream.transferTo(zos);
		zos.closeEntry();
	}

	private void addFileToZip(ZipOutputStream zos, ByteArrayResource resource, String fileName) throws IOException {
		zos.putNextEntry(new ZipEntry(fileName));
		zos.write(resource.getByteArray());
		zos.closeEntry();
	}

	// 로컬 파일의 내용을 문자열로 로드
	private String loadFileContent(String filePath) throws IOException {
		ClassPathResource resource = new ClassPathResource(filePath);
		InputStream inputStream = resource.getInputStream();
		return new String(inputStream.readAllBytes());
	}
}
