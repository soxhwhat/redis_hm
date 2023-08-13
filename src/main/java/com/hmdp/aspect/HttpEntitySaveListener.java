package com.hmdp.aspect;

import com.hmdp.entity.Blog;
import com.hmdp.entity.Follow;
import com.hmdp.service.excel.CompanyName;
import com.hmdp.service.excel.CompanyNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
@Component
public class HttpEntitySaveListener<T> implements EntitySaveListener<T> {
    @Autowired
    private CompanyNameService companyNameService;

    @Override
    public void afterEntitySave(T entity) {
        if (entity instanceof CompanyName) {

        } else if (entity instanceof Blog) {

        } else if (entity instanceof Follow)

    }

    @Override
    public void afterEntityUpdate(T entity) {

    }

    @Override
    public void afterEntitySaveBatch(List<T> entityList) {

    }

    @Override
    public void afterEntityUpdateBatch(List<T> entityList) {

    }

    @Override
    public void afterEntitySaveOrUpdateBatch(List<T> entityList) {

    }
}
package com.anker.mdp.Listener;


        import com.anker.mdp.dto.contract.CompanyContractDTO;
        import com.anker.mdp.dto.contract.CustomerSupplierVendorDTO;
        import com.anker.mdp.entity.CompanyInfo;
        import com.anker.mdp.entity.Customer;
        import com.anker.mdp.entity.SuppliersBasicInfo;
        import com.anker.mdp.helper.RequestContractHelper;
        import com.anker.mdp.service.CompanyInfoService;
        import com.anker.mdp.service.CustomerService;
        import com.anker.mdp.service.SuppliersBasicInfoService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Component;
        import org.springframework.util.CollectionUtils;

        import java.util.Collections;
        import java.util.List;

@Component
public class HttpEntitySaveListener<T> implements SaveUpdateListener<T> {
    @Autowired
    private SuppliersBasicInfoService suppliersBasicInfoService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CompanyInfoService companyInfoService;

    @Autowired
    private RequestContractHelper requestContractHelper;

    @Override
    public void afterEntitySave(T entity) {
        if (entity instanceof Customer) {
            customerService

        } else if (entity instanceof SuppliersBasicInfo) {

        } else if (entity instanceof CompanyInfo) {

        }

    }

    @Override
    public void afterEntityUpdate(T entity) {

    }

    @Override
    public void afterEntitySaveBatch(List<T> entityList) {
        //判断是否为空
        if (CollectionUtils.isEmpty(entityList)) {
            return;
        }
        if (entityList.get(0) instanceof Customer) {
            List<CustomerSupplierVendorDTO> customerSupplierVendorDTOList = customerService.convertToContractData((List<Customer>) entityList);
            requestContractHelper.requestVendor(customerSupplierVendorDTOList);
        } else if (entityList.get(0) instanceof SuppliersBasicInfo) {
            List<CustomerSupplierVendorDTO> customerSupplierVendorDTOList = suppliersBasicInfoService.convertToContractData((List<SuppliersBasicInfo>) entityList);
            requestContractHelper.requestVendor(customerSupplierVendorDTOList);

        } else if (entityList.get(0) instanceof CompanyInfo) {
            List<CompanyContractDTO> customerSupplierVendorDTOList = companyInfoService.convertToContractData((List<CompanyInfo>) entityList);
            requestContractHelper.requestCompany(customerSupplierVendorDTOList);
        }

    }

    @Override
    public void afterEntityUpdateBatch(List<T> entityList) {

    }

    @Override
    public void afterEntitySaveOrUpdateBatch(List<T> entityList) {

    }
}
package com.anker.mdp.Listener;

        import java.util.List;

public interface SaveUpdateListener<T> {
    void afterEntitySave(T entity);

    void afterEntityUpdate(T entity);

    void afterEntitySaveBatch(List<T> entityList);

    void afterEntityUpdateBatch(List<T> entityList);

    void afterEntitySaveOrUpdateBatch(List<T> entityList);

}
