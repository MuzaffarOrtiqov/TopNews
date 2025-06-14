package api.kun.uz.repository;

import api.kun.uz.entity.AttachEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttachRepository extends JpaRepository<AttachEntity,String> {
    @Query("FROM AttachEntity a WHERE a.visible=true ORDER BY a.createdDate DESC ")
    Page<AttachEntity> findAllAttaches(Pageable pageable);
}
