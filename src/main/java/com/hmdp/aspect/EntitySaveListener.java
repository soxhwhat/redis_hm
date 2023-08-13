package com.hmdp.aspect;

import com.hmdp.service.excel.CompanyName;
import org.springframework.stereotype.Component;

import java.util.List;
public interface EntitySaveListener<T> {
    void afterEntitySave(T entity);

    void afterEntityUpdate(T entity);

    void afterEntitySaveBatch(List<T> entityList);

    void afterEntityUpdateBatch(List<T> entityList);

    void afterEntitySaveOrUpdateBatch(List<T> entityList);


}
