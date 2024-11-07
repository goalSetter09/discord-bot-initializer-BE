package hongik.discordbots.initializer.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import hongik.discordbots.initializer.dto.FileDownloadResponse;
import hongik.discordbots.initializer.entity.Bot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileDownloadService {

	private final PythonFileService pythonFileService;
	private final BotService botService;

	public FileDownloadResponse generateCombinedPythonZip(List<Long> botIds) throws IOException {
		// 선택된 봇 엔티티 조회
		List<Bot> selectedBots = botService.getBotsByIds(botIds);
		log.info("Retrieved {} bots from the database for download.", selectedBots.size());

		// 각 봇의 S3 파일 경로 추출
		List<String> s3Paths = selectedBots.stream()
			.map(Bot::getS3Path)
			.collect(Collectors.toList());

		log.info("Retrieved {} S3 file paths.", s3Paths.size());

		// PythonFileService를 통해 ZIP 파일 생성
		return pythonFileService.createCombinedPythonZip(s3Paths);
	}
}
