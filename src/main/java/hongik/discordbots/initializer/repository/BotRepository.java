package hongik.discordbots.initializer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hongik.discordbots.initializer.entity.Bot;

@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {

	Optional<Bot> findByName(String name);
}
