//package com.hmdp.aspect;
//
//import com.hmdp.entity.Blog;
//import com.hmdp.entity.Follow;
//import com.hmdp.service.excel.CompanyName;
//import com.hmdp.service.excel.CompanyNameService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import java.util.List;
//@Component
//public class HttpEntitySaveListener<T> implements EntitySaveListener<T> {
//    @Autowired
//    private CompanyNameService companyNameService;
//
//    @Override
//    public void afterEntitySave(T entity) {
//        if (entity instanceof CompanyName) {
//
//        } else if (entity instanceof Blog) {
//
//        } else if (entity instanceof Follow)
//
//    }
//
//    @Override
//    public void afterEntityUpdate(T entity) {
//
//    }
//
//    @Override
//    public void afterEntitySaveBatch(List<T> entityList) {
//
//    }
//
//    @Override
//    public void afterEntityUpdateBatch(List<T> entityList) {
//
//    }
//
//    @Override
//    public void afterEntitySaveOrUpdateBatch(List<T> entityList) {
//
//    }
//}