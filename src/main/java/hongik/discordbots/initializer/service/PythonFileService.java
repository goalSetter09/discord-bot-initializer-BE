package hongik.discordbots.initializer.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import hongik.discordbots.initializer.dto.FileDownloadResponse;
import hongik.discordbots.initializer.s3.S3FileService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PythonFileService {

	private final S3FileService s3FileService;
	private static final String S3_BASE_PATH = "discord-bot/"; // S3 상의 기본 경로 설정
	private static final String[] additionalFiles = {"install_requirements.bat", "install_requirements.sh",
		"requirements.txt", "settings.py"};

	// Combined Python ZIP 파일 생성
	public FileDownloadResponse createCombinedPythonZip(List<String> s3Paths) throws IOException {
		ByteArrayResource mainPyResource = createCombinedPythonBotFile(s3Paths);
		byte[] zipData = createZipWithAdditionalFiles(mainPyResource);

		return new FileDownloadResponse("python_combined_environment.zip", "application/zip", zipData);
	}

	// main.py 파일 생성 (header, S3 내용, footer 포함)
	private ByteArrayResource createCombinedPythonBotFile(List<String> s3Paths) throws IOException {
		String header = s3FileService.getFileContent(S3_BASE_PATH + "header.py");
		String footer = s3FileService.getFileContent(S3_BASE_PATH + "footer.py");

		StringBuilder mainPyContent = new StringBuilder();
		mainPyContent.append(header).append("\n");

		for (String s3Path : s3Paths) {
			String botContent = s3FileService.getFileContent(S3_BASE_PATH + s3Path);
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
			String fileContent = s3FileService.getFileContent(S3_BASE_PATH + fileName);
			if (fileContent != null) {
				addFileToZip(zos, new ByteArrayResource(fileContent.getBytes()), fileName);
			}
		}

		zos.finish();
		return baos.toByteArray();
	}

	private void addFileToZip(ZipOutputStream zos, ByteArrayResource resource, String fileName) throws IOException {
		zos.putNextEntry(new ZipEntry(fileName));
		zos.write(resource.getByteArray());
		zos.closeEntry();
	}
}
