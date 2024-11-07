package hongik.discordbots.initializer.dto;

public record BotResponse(
	Long id,
	String name,
	String description
) {
	public static BotResponse from(hongik.discordbots.initializer.entity.Bot bot) {
		return new BotResponse(bot.getId(), bot.getName(), bot.getDescription());
	}
}
