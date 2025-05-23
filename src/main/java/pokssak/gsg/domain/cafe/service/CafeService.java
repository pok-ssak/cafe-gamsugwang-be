package pokssak.gsg.domain.cafe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.cafe.repository.CafeRepository;

@RequiredArgsConstructor
@Service
public class CafeService {

    private final CafeRepository cafeRepository;

    public Cafe getCafeById(Long cafeId) {
        return cafeRepository.findById(cafeId).get();
    }

}
