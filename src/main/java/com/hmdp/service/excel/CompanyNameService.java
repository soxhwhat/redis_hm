package com.hmdp.service.excel;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.aspect.EntitySaveListener;
import com.hmdp.mapper.CompanyNameMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CompanyNameService extends ServiceImpl<CompanyNameMapper, CompanyName> {
//    private final EntitySaveListener<CompanyName> entitySavedEventListener;
//
//    public CompanyNameService(EntitySaveListener<CompanyName>  entitySavedEventListener) {
//        this.entitySavedEventListener = entitySavedEventListener;
//    }


//    @Override
//    public boolean save(CompanyName entity) {
//        boolean success =  super.save(entity);
//        if (success) {
//            entitySavedEventListener.afterEntitySave(Arrays.asList(entity));
//        }
//        return success;
//    }
}
