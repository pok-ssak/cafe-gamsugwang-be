package pokssak.gsg.domain.upload.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.common.s3.S3Uploader;

import java.net.URI;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/upload")
@RestController
public class UploadController {

    private final S3Uploader s3Uploader;

    @PostMapping("")
    public ResponseEntity<?> upload(
            @RequestPart(value = "file", required = true) MultipartFile file
    ) {
        log.info("fileName={}, fileType={}, fileSize={}", file.getOriginalFilename(), file.getContentType(), file.getSize());
        var url = s3Uploader.upload(file);
        return ResponseEntity.created(URI.create(url))
                .body(ApiResponse.ok(Map.of("url", url)));
    }
}
