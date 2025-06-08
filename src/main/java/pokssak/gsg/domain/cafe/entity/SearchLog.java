package pokssak.gsg.domain.cafe.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "search_logs")
@Builder
public class SearchLog {
    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String query;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Date)
    private Date timestamp;
}
