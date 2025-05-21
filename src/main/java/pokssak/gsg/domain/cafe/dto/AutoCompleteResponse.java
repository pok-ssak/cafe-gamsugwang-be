package pokssak.gsg.domain.cafe.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AutoCompleteResponse {
    private List<Data> items;


}

@Builder
class Data {
    private String title;
}
