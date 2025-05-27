package pokssak.gsg.domain.upload.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.s3.S3Uploader;
import pokssak.gsg.domain.upload.exception.UploadErrorCode;

import java.net.URI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadControllerTest {

    @Mock
    S3Uploader s3Uploader;

    @InjectMocks
    UploadController uploadController;

    @Nested
    @DisplayName("업로드")
    class Upload {

        @Test
        @DisplayName("성공")
        void upload_success() {
            // given
            var file = new MockMultipartFile(
                    "image",
                    "hello.jpg",
                    "image/jpg",
                    new byte[0]
            );
            var mockUri = "test";
            when(s3Uploader.upload(any(MultipartFile.class))).thenReturn(mockUri);

            // when
            var result = uploadController.upload(file);

            // then
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getHeaders().getLocation()).isEqualTo(URI.create(mockUri));
            assertThat(result.getBody()).isInstanceOf(ApiResponse.class);
        }

        @Test
        @DisplayName("이미지 파일이 아닌 경우")
        void upload_not_an_image() {
            // given
            var file = new MockMultipartFile(
                    "image",
                    "hello.txt",
                    "text/plain",
                    new byte[0]
            );

            // when & then
            assertThatThrownBy(() -> uploadController.upload(file))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", UploadErrorCode.NOT_AN_IMAGE);
        }

        @Test
        @DisplayName("S3Uploader 예외 발생")
        void upload_s3Uploader_fail() {
            // given
            var file = new MockMultipartFile(
                    "image",
                    "hello.jpg",
                    "image/jpeg",
                    new byte[0]
            );

            when(s3Uploader.upload(any(MultipartFile.class)))
                    .thenThrow(new RuntimeException());

            // when & then
            assertThatThrownBy(() -> uploadController.upload(file))
                    .isInstanceOf(RuntimeException.class);
        }
    }
}
