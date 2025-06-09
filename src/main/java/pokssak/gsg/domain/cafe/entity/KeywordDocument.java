package pokssak.gsg.domain.cafe.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;
import java.util.List;


@Document(indexName = "keyword")
@Setting(settingPath = "es/analysis-settings.json")
@Getter
@Builder
public class KeywordDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String keyword;

    @Field(type = FieldType.Integer)
    private Integer count;

    @Setter
    @Field(type = FieldType.Dense_Vector,
            dims = 768,
            index = true,
            similarity = "cosine")
    private List<Float> keywordVector; // 본 도큐먼트의 "keyword" 필드를, embedding

//    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

//    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Field(type = FieldType.Date)
    private LocalDateTime modifiedAt;

//    public static KeywordDocument from(Keyword keyword) {
//
//        return KeywordDocument.builder()
//                .id(keyword.getId())
//                .keyword(keyword.getKeyword())
//                .count(keyword.getCount())
//                .createdAt(keyword.getCreatedAt())
//                .modifiedAt(keyword.getModifiedAt())
//                .build();
//    }
}
