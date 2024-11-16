package hongik.discordbots.initializer.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

	private final S3FileService s3FileService;
	private static final String S3_BASE_PATH = "discord-bot/"; // S3 상의 기본 경로 설정
	// private static final String[] additionalFiles = {"install_requirements.bat", "install_requirements.sh",
	// 	"requirements.txt", "settings.py"};
	private static final String[] STATIC_FILES = {"Builder.spec", "launcher.py", "main.py", "settings.py"};
	private static final String[] STATIC_DIRS = {"docs/"};
	private static final String ZIP_FILE_NAME = "discord-bot.zip";

	// Combined Python ZIP 파일 생성
	public FileDownloadResponse createCombinedPythonZip(List<String> selectedCogs) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		addStaticFiles(zos);
		addCogsFromS3(zos, selectedCogs);
		addStaticDirectories(zos);

		zos.finish();
		return new FileDownloadResponse(ZIP_FILE_NAME, "application/zip", baos.toByteArray());
	}

	private void addStaticFiles(ZipOutputStream zos) throws IOException {
		for (String fileName : STATIC_FILES) {
			String fileContent = getResourceFileContent(fileName);
			addFileToZip(zos, new ByteArrayResource(fileContent.getBytes()), fileName);
		}
	}

	private String getResourceFileContent(String filePath) throws IOException {
		ClassPathResource resource = new ClassPathResource(filePath);
		InputStream inputStream = resource.getInputStream();
		return new String(inputStream.readAllBytes());
	}

	private void addCogsFromS3(ZipOutputStream zos, List<String> selectedCogs) throws IOException {
		for (String cogFileName : selectedCogs) {
			String cogContent = s3FileService.getFileContent(S3_BASE_PATH + cogFileName);
			addFileToZip(zos, new ByteArrayResource(cogContent.getBytes()), "cogs/" + cogFileName);
		}
	}

	private void addStaticDirectories(ZipOutputStream zos) throws IOException {
		for (String dir : STATIC_DIRS) {
			addDirectoryToZip(zos, dir);
		}
	}

	private void addFileToZip(ZipOutputStream zos, ByteArrayResource resource, String fileName) throws IOException {
		zos.putNextEntry(new ZipEntry(fileName));
		zos.write(resource.getByteArray());
		zos.closeEntry();
	}

	private void addDirectoryToZip(ZipOutputStream zos, String directoryPath) throws IOException {
		ClassPathResource directoryResource = new ClassPathResource(directoryPath);
		File directory = directoryResource.getFile();
		if (directory.isDirectory()) {
			for (File file : directory.listFiles()) {
				String filePath = directoryPath + file.getName();
				String fileContent = getResourceFileContent(filePath);
				addFileToZip(zos, new ByteArrayResource(fileContent.getBytes()), filePath);
			}
		}
	}

	// // main.py 파일 생성 (header, S3 내용, footer 포함)
	// private ByteArrayResource createCombinedPythonBotFile(List<String> s3Paths) throws IOException {
	// 	String header = s3FileService.getFileContent(S3_BASE_PATH + "header.py");
	// 	String footer = s3FileService.getFileContent(S3_BASE_PATH + "footer.py");
	//
	// 	StringBuilder mainPyContent = new StringBuilder();
	// 	mainPyContent.append(header).append("\n");
	//
	// 	for (String s3Path : s3Paths) {
	// 		String botContent = s3FileService.getFileContent(S3_BASE_PATH + s3Path);
	// 		mainPyContent.append(botContent).append("\n");
	// 	}
	//
	// 	mainPyContent.append(footer);
	// 	return new ByteArrayResource(mainPyContent.toString().getBytes());

	// }
	// // ZIP 파일 생성 및 추가 파일 포함
	// private byte[] createZipWithAdditionalFiles(ByteArrayResource mainPyResource) throws IOException {
	// 	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// 	ZipOutputStream zos = new ZipOutputStream(baos);
	//
	// 	addFileToZip(zos, mainPyResource, "main.py");
	//
	// 	for (String fileName : additionalFiles) {
	// 		String fileContent = s3FileService.getFileContent(S3_BASE_PATH + fileName);
	// 		if (fileContent != null) {
	// 			addFileToZip(zos, new ByteArrayResource(fileContent.getBytes()), fileName);
	// 		}
	// 	}
	//
	// 	zos.finish();
	// 	return baos.toByteArray();

	// }
}
