package pokssak.gsg.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(globalApiResponses());
    }

    private Info apiInfo() {
        return new Info()
                .title("카페 감수광 API 문서")
                .description("GSG Swagger API Docs.")
                .version("1.0.0");
    }


    private Components globalApiResponses() {
        return new Components()
                .addResponses("403", new ApiResponse().description("권한 없음"))
                .addResponses("404", new ApiResponse().description("리소스 없음"))
                .addResponses("201", new ApiResponse().description("리소스 생성"))
                .addResponses("204", new ApiResponse().description("삭제 성공"));

    }

}
