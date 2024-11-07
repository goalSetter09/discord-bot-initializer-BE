package hongik.discordbots.initializer.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hongik.discordbots.initializer.dto.BotListResponse;
import hongik.discordbots.initializer.dto.BotResponse;
import hongik.discordbots.initializer.entity.Bot;
import hongik.discordbots.initializer.repository.BotRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BotService {

	private final BotRepository botRepository;

	public BotListResponse findAllBots() {
		List<BotResponse> list = botRepository.findAll().stream()
			.map(BotResponse::from)
			.toList();

		return BotListResponse.from(list);
	}

	public List<Bot> getBotsByIds(List<Long> ids) {
		return botRepository.findAllById(ids);
	}
}
