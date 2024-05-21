package hongik.discordbots.initializer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileDownloadResponse {
    private String filename;
    private String contentType;
    private byte[] fileData;
}
