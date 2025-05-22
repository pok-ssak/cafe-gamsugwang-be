package pokssak.gsg.domain.cafe.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pokssak.gsg.domain.cafe.repository.CafeESRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CafeService {
    private final CafeSearchService cafeSearchService;
    private final CafeESRepository cafeESRepository;

    public List<String> autoComplete(String keyword, int limit) {
        log.info("keyword: {}, limit: {}", keyword, limit);
        List<String> titles = cafeSearchService.suggestTitleByKeyword(keyword, limit);
        log.info("titleByPrefix = {}", titles);

        return titles;
    }
}
