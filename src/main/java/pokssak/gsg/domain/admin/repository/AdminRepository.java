package pokssak.gsg.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pokssak.gsg.domain.admin.entity.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
}