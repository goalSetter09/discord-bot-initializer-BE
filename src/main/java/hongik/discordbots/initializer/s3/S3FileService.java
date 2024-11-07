package hongik.discordbots.initializer.s3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class S3FileService {

	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucketName}")
	private String bucketName;

	private final static String botDirectoryPrefix = "discord-bot/";

	// S3 버킷에서 봇 파일 목록을 가져오는 메서드
	public List<String> listBotFiles() {
		ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
			.withBucketName(bucketName)
			.withPrefix(botDirectoryPrefix);  // 봇 파일이 저장된 경로

		ListObjectsV2Result listObjectsResult = amazonS3Client.listObjectsV2(listObjectsRequest);
		return listObjectsResult.getObjectSummaries().stream()
			.map(S3ObjectSummary::getKey)
			.filter(key -> !key.endsWith("/")) // 폴더 이름이 아닌 실제 파일만 남김
			.toList();
	}

	// S3에서 특정 파일의 내용을 가져오는 메서드
	public String getFileContent(String s3Path) throws IOException {
		S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, s3Path));
		InputStream inputStream = s3Object.getObjectContent();
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}
}
