package hongik.discordbots.initializer.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hongik.discordbots.initializer.dto.BotListResponse;
import hongik.discordbots.initializer.dto.FileDownloadResponse;
import hongik.discordbots.initializer.s3.S3FileService;
import hongik.discordbots.initializer.service.BotService;
import hongik.discordbots.initializer.service.FileDownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class FileDownloadController {

	private final FileDownloadService fileDownloadService;
	private final S3FileService s3FileService;
	private final BotService botService;

	@PostMapping("/download")
	public ResponseEntity<Resource> downloadCombinedPythonZip(
		@RequestParam("dependencies") List<Long> botIds) throws IOException {
		log.info("Download combined Python ZIP request received for bot IDs: {}", botIds);
		// 파일 생성 및 다운로드 응답 생성
		FileDownloadResponse response = fileDownloadService.generateCombinedPythonZip(botIds);
		ByteArrayResource resource = new ByteArrayResource(response.getFileData());

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFilename() + "\"")
			.contentType(MediaType.parseMediaType(response.getContentType()))
			.body(resource);
	}

	@GetMapping("/home")
	public String home(Model model) {
		BotListResponse botList = botService.findAllBots();
		model.addAttribute("bots", botList.bots());
		log.info("Retrieved {} bots from the database.", botList.bots().size());
		return "home";
	}
}
