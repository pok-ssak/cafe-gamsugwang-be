package pokssak.gsg.domain.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoCompleteResponse {
    private Long id;
    private String title;


    public static AutoCompleteResponse from(Long id, String title) {
        return AutoCompleteResponse.builder()
                .id(id)
                .title(title)
                .build();
    }
}
