package pokssak.gsg.domain.cafe.entity;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.math.BigDecimal;
import java.util.List;

@Document(indexName = "cafe")
@Setting(settingPath = "es/analysis-settings.json")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CafeDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @MultiField(
            mainField = @Field(
                    type          = FieldType.Text,
                    analyzer      = "edge_ngram_analyzer",
                    searchAnalyzer= "standard_search"
            ),
            otherFields = {
                    @InnerField(
                            suffix = "keyword",
                            type   = FieldType.Keyword,
                            ignoreAbove = 256
                    )
            }
    )
    private String title;

    @Field(type = FieldType.Keyword)
    private String imgUrl;

    @Field(type = FieldType.Integer)
    private Integer reviewCount;

//    @Field(type = FieldType.Text)
//    private String info;

    @Field(type = FieldType.Double)
    private BigDecimal rate;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    //@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private String createdAt;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String modifiedAt;

    @Field(type = FieldType.Nested)
    private List<Menu> menus;

    @Field(type = FieldType.Nested)
    private Address address;

    @Field(type = FieldType.Nested)
    private List<Keyword> keywords;

    @Field(type = FieldType.Dense_Vector,
    dims = 1536,
    index = true,
    similarity = "cosine")
    private float[] keywordVector;

    @Getter
    @Builder
    public static class Menu {
        @Field(type = FieldType.Text)
        private String keyword;
        @Field(type = FieldType.Keyword)
        private String image;
        @Field(type = FieldType.Integer)
        private int price;
    }

    @Getter
    @Builder
    public static class Address {
        @Field(type = FieldType.Text)
        private String street;
        @Field(type = FieldType.Keyword)
        private String zipCode;
        @GeoPointField
        private GeoPoint location;
    }

    @Getter
    @Builder
    public static class Keyword {
        @Field(type = FieldType.Text)
        private String key;
        @Field(type = FieldType.Integer)
        private Integer count;
    }

    public void updateKeywordVector(float[] vector) {
        this.keywordVector = vector;
    }
}