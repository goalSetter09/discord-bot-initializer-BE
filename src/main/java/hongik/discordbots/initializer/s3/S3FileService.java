package hongik.discordbots.initializer.s3;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class S3FileService {

	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucketName}")
	private String bucketName;

	// S3 버킷에서 봇 파일 목록을 가져오는 메서드
	public List<String> listBotFiles() {
		ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
			.withBucketName(bucketName)
			.withPrefix("discord-bot/");  // 봇 파일이 저장된 경로

		ListObjectsV2Result listObjectsResult = amazonS3Client.listObjectsV2(listObjectsRequest);
		return listObjectsResult.getObjectSummaries().stream()
			.map(S3ObjectSummary::getKey)
			.toList();
	}
}
