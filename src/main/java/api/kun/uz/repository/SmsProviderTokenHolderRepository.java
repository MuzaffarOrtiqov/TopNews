package api.kun.uz.repository;

import api.kun.uz.entity.SmsProviderTokenHolderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SmsProviderTokenHolderRepository extends CrudRepository<SmsProviderTokenHolderEntity,Integer > {
    Optional<SmsProviderTokenHolderEntity> findFirstByOrderByIdDesc();
}
