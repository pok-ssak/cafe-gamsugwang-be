package pokssak.gsg.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.transaction.PlatformTransactionManager;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.cafe.entity.CafeDocument;
import pokssak.gsg.domain.cafe.repository.CafeESRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class CafeBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory emf;
    private final CafeESRepository cafeESRepository;
    private final EmbeddingModel embeddingModel;

    private static final DateTimeFormatter ES_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * 1. JpaCursorItemReader 정의
     * - JOIN FETCH를 사용하여 연관된 메뉴와 키워드를 함께 읽어옴
     */
    @Bean
    public JpaCursorItemReader<Cafe> cafeReader(){
        return new JpaCursorItemReaderBuilder<Cafe>()
                .name("cafeReader")
                .entityManagerFactory(emf)
                .queryString("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.menuList LEFT JOIN FETCH c.keywordList")
                .build();
    }

    /**
     * Step2: Cafe → CafeDocument 변환
     * - 날짜는 Elasticsearch 매핑에 맞춰 "yyyy-MM-dd'T'HH:mm:ss" 포맷
     * - 벡터(keywordVector)는 Writer 단계에서 세팅
     */
    @Bean
    public ItemProcessor<Cafe, CafeDocument> cafeProcessor() {



        return cafe -> {
            GeoPoint geoPoint = new GeoPoint(cafe.getLat().doubleValue(), cafe.getLon().doubleValue());
            log.info("Processing Cafe: {}, Location: {}", cafe.getId(), geoPoint);
            return CafeDocument.builder()
                    .id(cafe.getId())
                    .title(cafe.getTitle())
                    .rate(cafe.getRate())
                    .imgUrl(cafe.getImageUrl())
                    .reviewCount(cafe.getRateCount())
                    .createdAt(cafe.getCreatedAt().format(ES_DATE_FORMAT))
                    .modifiedAt(cafe.getModifiedAt().format(ES_DATE_FORMAT))
                    .address(CafeDocument.Address.builder()
                            .street(cafe.getAddress())
                            .zipCode(cafe.getZipcode())
                            .location(new GeoPoint(cafe.getLat().doubleValue(), cafe.getLon().doubleValue()))
                            .build())
                    .menus(cafe.getMenuList().stream()
                            .map(m -> CafeDocument.Menu.builder()
                                    .keyword(m.getName())
                                    .price(m.getPrice())
                                    .image(m.getMenuImageUrl())
                                    .build())
                            .collect(Collectors.toList()))
                    .keywords(cafe.getCafeKeywordList().stream()
                            .map(k -> CafeDocument.Keyword.builder()
                                    .key(k.getKeyword())
                                    .count(k.getCount())
                                    .build())
                            .collect(Collectors.toList()))
                    // .keywordVector는 Writer 단계에서 세팅
                    .build();
        };
    }

    /**
     * Step3: 배치 Writer
     * - Chunk 단위로 임베딩을 한 번에 처리(embed(List<String>))
     * - 결과 벡터를 각 CafeDocument에 세팅 후 saveAll()
     */
    @Bean
    public ItemWriter<CafeDocument> cafeWriter() {
        return docs -> {
            List<? extends CafeDocument> items = docs.getItems();

            List<String> texts = items.stream()
                    .map(doc -> {
                        String menuText = doc.getMenus().stream()
                                .map(CafeDocument.Menu::getKeyword)
                                .collect(Collectors.joining(" "));
                        String kwText = doc.getKeywords().stream()
                                .map(CafeDocument.Keyword::getKey)
                                .collect(Collectors.joining(" "));
                        return doc.getTitle() + " " + menuText + " " + kwText;
                    })
                    .collect(Collectors.toList());

            List<float[]> vectors = embeddingModel.embed(texts);

            for (int i = 0; i < items.size(); i++) {
                items.get(i).updateKeywordVector(vectors.get(i));
            }

            cafeESRepository.saveAll(items);

            log.info("Chunk of size {} processed and upserted with embeddings", items.size());
        };
    }

    @Bean
    public StepExecutionListener stepLoggingListener() {
        return new StepExecutionListenerSupport() {
            @Override
            public void beforeStep(org.springframework.batch.core.StepExecution stepExecution) {
                log.info("[STEP START] name={}, startTime={}", stepExecution.getStepName(),stepExecution.getStartTime());
            }
            @Override
            public org.springframework.batch.core.ExitStatus afterStep(org.springframework.batch.core.StepExecution stepExecution) {
                log.info("[STEP END] {} 읽음={} 처리됨={}",
                        stepExecution.getStepName(),
                        stepExecution.getReadCount(),
                        stepExecution.getWriteCount());
                return stepExecution.getExitStatus();
            }
        };
    }

    @Bean
    public Step cafeStep() {
        return new StepBuilder("cafeStep", jobRepository)
                .<Cafe, CafeDocument>chunk(200, transactionManager)
                .reader(cafeReader())
                .processor(cafeProcessor())
                .writer(cafeWriter())
                .listener(stepLoggingListener())
                .listener(readListener())
                .build();
    }

    @Bean
    public Job cafeJob() {
        return new JobBuilder("cafeSyncJob", jobRepository)
                .start(cafeStep())
                .build();
    }

    @Bean
    public ItemReadListener<Cafe> readListener() {
        return new ItemReadListener<>() {
            @Override public void beforeRead()          { log.debug("아이템 읽기 시작"); }
            @Override public void afterRead(Cafe item)  { log.debug("읽은 아이템: {}", item.getId()); }
            @Override public void onReadError(Exception ex) { log.error("읽기 실패", ex); }
        };
    }
}
