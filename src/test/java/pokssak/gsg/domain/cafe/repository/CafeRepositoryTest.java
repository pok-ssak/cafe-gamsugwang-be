package pokssak.gsg.domain.cafe.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.cafe.entity.Keyword;
import pokssak.gsg.domain.cafe.entity.Menu;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CafeRepositoryTest {
    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private KeywordRepository keywordRepository;

    @Test
    public void findByIdWithMenusAndKeywords() {
        // Given
        Cafe cafe = Cafe.builder()
                .title("테스트카페")
                .build();

        Menu menu1 = Menu.builder()
                .name("아메리카노")
                .cafe(cafe)
                .build();
        Menu menu2 = Menu.builder()
                .name("카페라떼")
                .cafe(cafe)
                .build();

        Keyword keyword1 = Keyword.builder()
                .keyword("커피")
                .cafe(cafe)
                .build();
        Keyword keyword2 = Keyword.builder()
                .keyword("디저트")
                .cafe(cafe)
                .build();

        cafe.addMenu(menu1);
        cafe.addMenu(menu2);
        cafe.addKeyword(keyword1);
        cafe.addKeyword(keyword2);

        cafeRepository.save(cafe);
        menuRepository.saveAll(List.of(menu1, menu2));
        keywordRepository.saveAll(List.of(keyword1, keyword2));

        // when
        Cafe savedCafe = cafeRepository.findByIdWithMenusAndKeywords(cafe.getId()).get();

        // then
        assertThat(savedCafe).isNotNull()
                .extracting(Cafe::getTitle)
                .isEqualTo("테스트카페");
        assertThat(savedCafe.getMenuList()).hasSize(2)
                .extracting(Menu::getName)
                .containsExactlyInAnyOrder("아메리카노", "카페라떼");
        assertThat(savedCafe.getKeywordList()).hasSize(2)
                .extracting(Keyword::getKeyword)
                .containsExactlyInAnyOrder("커피", "디저트");
    }
}