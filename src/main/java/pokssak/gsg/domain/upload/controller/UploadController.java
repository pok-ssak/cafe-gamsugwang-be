package pokssak.gsg.domain.upload.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.s3.S3Uploader;
import pokssak.gsg.domain.upload.exception.UploadErrorCode;
import java.net.URI;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/upload")
@RestController
public class UploadController {

    private final S3Uploader s3Uploader;

    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> upload(
            @RequestPart(value = "image", required = true) MultipartFile image
    ) {
        log.info("fileName={}, fileType={}, fileSize={}",
                image.getOriginalFilename(), image.getContentType(), image.getSize());
        var type = image.getContentType();

        if (!type.startsWith("image/"))
            throw new CustomException(UploadErrorCode.NOT_AN_IMAGE);

        var url = s3Uploader.upload(image);
        return ResponseEntity.created(URI.create(url))
                .body(ApiResponse.ok(Map.of("url", url)));
    }
}
