package hongik.discordbots.initializer.dto;

import java.util.List;

public record BotListResponse(
	List<BotResponse> bots
) {
	public static BotListResponse from(List<BotResponse> botResponseList) {
		return new BotListResponse(botResponseList);
	}
}
