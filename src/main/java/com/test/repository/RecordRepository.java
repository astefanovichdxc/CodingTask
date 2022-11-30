package com.test.repository;

import com.test.model.RecordEntity;
import org.springframework.data.repository.CrudRepository;

public interface RecordRepository extends CrudRepository<RecordEntity, String> {

    RecordEntity findByPrimaryKey(String primaryKey);

    boolean existsByPrimaryKey(String primaryKey);

}
