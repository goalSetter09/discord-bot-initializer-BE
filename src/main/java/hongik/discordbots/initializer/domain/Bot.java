package hongik.discordbots.initializer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bot {

	@Id
	@GeneratedValue
	private Long id;

	// 봇 파일의 이름
	@Column(nullable = false, unique = true)
	private String name;

	// 봇 파일에 대한 설명
	@Column(length = 500)
	private String description;

	// S3에 저장된 파일 경로
	@Column(name = "s3_path", nullable = false)
	private String s3Path;
}
