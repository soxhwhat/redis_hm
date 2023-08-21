package com.hmdp.controller;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.UserInfo;
import com.hmdp.service.IUserInfoService;
import com.hmdp.service.IUserService;
import com.hmdp.service.excel.CompanyName;
import com.hmdp.service.excel.CompanyNameService;
import com.hmdp.service.excel.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;
    @Autowired
    private CompanyNameService companyNameService;

    @Autowired
    private HttpRequest httpRequest;

    @GetMapping("/test")
    public Result test() {
        CompanyName companyName = new CompanyName().setName("test").setUrl("111");
        CompanyName companyName1 = new CompanyName().setName("test1").setUrl("111");
        ArrayList<CompanyName> list = new ArrayList<>();
        list.add(companyName);
        list.add(companyName1);
//        companyNameService.saveBatch(list);
        companyNameService.save(companyName);
        return Result.ok();
    }


    /**
     * 发送手机验证码
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        // TODO 发送短信验证码并保存验证码
        return Result.fail("功能未完成");
    }

    /**
     * 登录功能
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session){
        // TODO 实现登录功能
        return Result.fail("功能未完成");
    }

    /**
     * 登出功能
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout(){
        // TODO 实现登出功能
        return Result.fail("功能未完成");
    }

    @GetMapping("/me")
    public Result me(){
        // TODO 获取当前登录的用户并返回
        return Result.fail("功能未完成");
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId){
        // 查询详情
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        // 返回
        return Result.ok(info);
    }
}


package com.anker.mdp.mapper;

        import com.anker.mdp.dto.contract.CustomerSupplierVendorDTO;
        import com.anker.mdp.entity.SuppliersBasicInfo;
        import org.mapstruct.Mapper;
        import org.mapstruct.Mapping;
        import org.mapstruct.Named;
        import org.mapstruct.factory.Mappers;

        import java.util.Date;
        import java.util.Objects;

/**
 * SupplierVendorMapper
 *
 * @author jaffe.huang@anker-in.com
 * @version 2023/08/07 16:50
 **/
@Mapper
public interface SupplierVendorMapper {
    /**
     * 成功示例
     * {
     *   "vendor": "F01411118",
     *   "vendorNature":"0",
     *   "vendorText": "Dolby Labor111atories Licensing Corporation",
     *   "vendorType": "2",
     *   "adCountry": "US",
     *   "certificationId": "205823646111180480",
     *   "certificationType": "3",
     *   "status": 1
     * }
     *
     *系统配置字段[%s]不能为空（无adCountry）
     * {
     *   "vendor": "F01411118",
     *   "vendorNature":"0",
     *   "vendorText": "Dolby Labor111atories Licensing Corporation",
     *   "vendorType": "2",
     *
     *   "certificationId": "205823646111180480",
     *   "certificationType": "3",
     *   "status": 1
     * }
     *
     * 非中国大陆法人或企业只能使用注册号或税号（有adCountry，但不是CN）
     * {
     *   "vendor": "F01411118",
     *   "vendorNature":"0",
     *   "vendorText": "Dolby Labor111atories Licensing Corporation",
     *   "vendorType": "2",
     *   "adCountry": "US",
     *   "certificationId": "205823646111180480",
     *   "certificationType": "0",
     *   "status": 1
     * }
     */
    SupplierVendorMapper INSTANCE = Mappers.getMapper(SupplierVendorMapper.class);

    @Mapping(source = "companyCode", target = "vendor")
    @Mapping(source = "companyNature", target = "vendorNature", qualifiedByName = "mapCompanyNatureToVendorNature")
    @Mapping(source = "companyName", target = "vendorText")
    @Mapping(target = "vendorType", constant = "2")
    @Mapping(source = "ext", target = "adCountry")
    @Mapping(source = "cetcSourceAddress", target = "address")
    @Mapping(target = "certificationType", expression = "java(mapCertificationType(suppliersBasicInfo))")
    @Mapping(source = "creditCode", target = "certificationId")
    @Mapping(target = "status", expression = "java(mapBusinessDateStatus(suppliersBasicInfo))")
    CustomerSupplierVendorDTO supplierToCustomerSupplierVendorDTO(SuppliersBasicInfo suppliersBasicInfo);

    @Named("mapCompanyNatureToVendorNature")
    default String mapCompanyNatureToVendorNature(String companyNature) {
        //不能为空，示例数据
        //1601151134944976897	JIXL01，有很多数据的companyNature是空的
        //所以数据库中不存在的值应该如何处理
        /**
         * 无companyNature请求失败
         * {
         *   "vendor": "F014321411118",
         *   "vendorText": "Dol342by La1bor111atories Licensing Corporation",
         *   "vendorType": "2",
         *   "adCountry": "US",
         *
         *   "status": 1
         * }
         *   "msg": "系统配置字段[%s]不能为空",
         *
         */
        if ("私营企业".equals(companyNature) || "国有企业".equals(companyNature) || "集体企业".equals(companyNature) ||
                "中外合资企业".equals(companyNature) || "外资企业".equals(companyNature) || "其他组织".equals(companyNature)) {
            return "0";
        } else if ("个体户".equals(companyNature)) {
            return "1";
        } else if ("非营利机构".equals(companyNature)) {
            return "2";
        } else {
            return "0";
        }
    }
    @Named("mapBusinessDateStatus")
    default int mapBusinessDateStatus(SuppliersBasicInfo suppliersBasicInfo) {
        Date currentDate = new Date();
        Date businessDateFrom = suppliersBasicInfo.getBusinessDateFrom();
        Date businessDateTo = suppliersBasicInfo.getBusinessDateTo();

        if (businessDateFrom != null && businessDateTo != null) {
            if (currentDate.compareTo(businessDateFrom) >= 0 && currentDate.compareTo(businessDateTo) <= 0) {
                return 1;
            } else if (currentDate.before(businessDateFrom) || currentDate.after(businessDateTo)) {
                return 0;
            }
        }
        return 0;
    }

    @Named("mapCertificationType")
    //0：统一社会信用代码(中国大陆)
    //1：中国大陆居民身份证(中国大陆)
    //2：注册号(海外)
    //3：税号(海外)
    //4：驾驶证(海外)
    //和adCountry有关，如果adCountry默认为空的话，就认为是海外的，所以creditCode不能赋值为0，只能为2或者3（和数据无关，目前不存在adCountry为空的情况）
    // adCountry和creditCode不能都为空
    /**
     *     "msg": "系统配置字段[%s]不能为空",
     * {
     *     "vendor": "F01141011118",
     *     "vendorNature": "0",
     *     "vendorText": "Dolby La1bor111atories Licensing Corporation",
     *     "vendorType": "2",
     *     "certificationId": "20581236146111180480",
     *     "status": 1
     * }
     * 只有adCountry，没有creditCode可以请求成功
     * {
     *     "vendor": "F011411118",
     *     "vendorNature": "0",
     *     "vendorText": "Dolby La1bor111atories Licensing Corporation",
     *     "vendorType": "2",
     *     "adCountry": "US",
     *     "certificationId": "2058123646111180480",
     *     "status": 1
     * }
     *
     * 没有creditCode和certificationId可以请求成功
     * {
     *   "vendor": "F014321411118",
     *   "vendorNature": "0",
     *   "vendorText": "Dol342by La1bor111atories Licensing Corporation",
     *   "vendorType": "2",
     *   "adCountry": "US",
     *
     *   "status": 1
     * }
     */
    default String mapCertificationType(SuppliersBasicInfo suppliersBasicInfo) {
        // 只有当adCountry为CN的时候以及creditCode不为空的时候才能赋值为0
        //当adCountry不为CN的时候，creditCode不为空的时候才能赋值为3
        if (Objects.equals(suppliersBasicInfo.getExt(), "CN") && Objects.nonNull(suppliersBasicInfo.getCreditCode())) {
            return "0";
        } else if (!Objects.equals(suppliersBasicInfo.getExt(), "CN") && Objects.nonNull(suppliersBasicInfo.getCreditCode())) {
            return "3";
        } else {
            return null;
        }

    }

}

package com.anker.mdp.service;

        import com.alibaba.fastjson.JSON;
        import com.alibaba.fastjson.JSONArray;
        import com.alibaba.fastjson.JSONObject;
        import com.alibaba.fastjson.serializer.SerializerFeature;
        import com.anker.mdp.Listener.SuppliersBasicInfoPersistenceListener;
        import com.anker.mdp.constant.AsyncConstant;
        import com.anker.mdp.dto.contract.CustomerSupplierVendorDTO;
        import com.anker.mdp.entity.*;
        import com.anker.mdp.mapper.SupplierVendorMapper;
        import com.anker.mdp.mapper.SuppliersBasicInfoMapper;
        import com.anker.mdp.mq.MQSupport;
        import com.anker.mdp.util.ContractPersistenceUtil;
        import com.anker.mdp.util.JsonUtils;
        import com.anker.mdp.util.MyQueryWrapper;
        import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
        import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
        import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import java.util.*;
        import java.util.function.Function;
        import java.util.stream.Collectors;

/**
 * <p>
 *   服务实现类
 * </p>
 *
 * @author Mybatis Plus Code Generator
 * @since 2022-09-05
 */
@Service
@Slf4j
public class SuppliersBasicInfoService extends ServiceImpl<SuppliersBasicInfoMapper, SuppliersBasicInfo> {

    @Autowired
    private MQSupport mqSupport;
    @Autowired
    private SuppliersAddressService suppliersAddressService;
    @Autowired
    private SuppliersContactService suppliersContactService;
    @Autowired
    private SuppliersFinancialService suppliersFinancialService;
    @Autowired
    private SuppliersBankAccountService suppliersBankAccountService;
    @Autowired
    private SuppliersFactoryService suppliersFactoryService;
    @Autowired
    private CustomerSupplierRelaService customerSupplierRelaService;
    @Autowired
    private SuppliersBasicInfoPersistenceListener suppliersBasicInfoPersistenceListener;

    public JSONObject search(JSONObject jsonObject) throws Exception {
        List<SuppliersBasicInfo> suppliersBasicInfos=this.list(MyQueryWrapper.toAbstractWrapper(jsonObject,SuppliersBasicInfo.class,true));
        long total=this.count(MyQueryWrapper.toAbstractWrapper(jsonObject,SuppliersBasicInfo.class,false));
        JSONArray jsonArray=new JSONArray();
        if(!org.apache.commons.collections.CollectionUtils.isEmpty(suppliersBasicInfos)) {
            suppliersBasicInfos.forEach(o -> {
                JSONObject object = JSON.parseObject(JSON.toJSONString(o));
                jsonArray.add(object);
            });
        }
        return MyQueryWrapper.toTotalList(total,jsonArray);
    }


    @Transactional(rollbackFor = Exception.class)
    public Result save(JSONArray jsonArray){
        if(CollectionUtils.isEmpty(jsonArray)){
            return Result.error("参数不能为空");
        }
        try {
            return toSuppliersBasicInfo(jsonArray);
        }catch (Exception e){
            log.error("toSuppliersBasicInfo: ",e);
            return Result.error(e.getMessage());
        }
    }

    private Result toSuppliersBasicInfo(JSONArray jsonArray){
        List<SuppliersBasicInfo> list=new ArrayList<>();

        Map<Long, String> groupLeaderMap = new HashMap<>();
        JsonUtils.convert(jsonArray);
        for (int i=0;i<jsonArray.size();i++){
            if("是".equals(jsonArray.getJSONObject(i).getString("isGroupLeader"))){
                groupLeaderMap.putIfAbsent(jsonArray.getJSONObject(i).getLong("groupId"),jsonArray.getJSONObject(i).getString("companyCode"));
            }
        }
        for(int j=0;j<jsonArray.size();j++){
            SuppliersBasicInfo suppliersBasicInfo=JSON.toJavaObject(jsonArray.getJSONObject(j),SuppliersBasicInfo.class);
            Long groupId=jsonArray.getJSONObject(j).getLong("groupId");
            if("否".equals(jsonArray.getJSONObject(j).getString("isGroupLeader"))){
                suppliersBasicInfo.setParentCompanyCode(groupLeaderMap.get(groupId));
            }else {
                suppliersBasicInfo.setParentCompanyCode(null);
            }
            list.add(suppliersBasicInfo);
        }
        List<String> codes = new ArrayList<>();
        for(SuppliersBasicInfo s:list){
            codes.add(s.getCompanyCode());
        }
        Map<String,SuppliersBasicInfo> map=new HashMap<>();
        if(CollectionUtils.isNotEmpty(codes)) {
            List<SuppliersBasicInfo> list2 = this.list(new QueryWrapper<SuppliersBasicInfo>().lambda().in(SuppliersBasicInfo::getCompanyCode, codes));
            map=list2.stream().collect(Collectors.toMap(SuppliersBasicInfo::getCompanyCode, Function.identity(), (v1, v2)->v1));
        }

        for(int i=0;i< list.size();i++){
            SuppliersBasicInfo o=list.get(i);
            SuppliersBasicInfo suppliersBasicInfo=map.get(o.getCompanyCode());
            if(suppliersBasicInfo==null) {
                o.setId(null);
                this.save(o);
            }else{
                o.setId(suppliersBasicInfo.getId());
                this.updateById(o);
            }
        }

        return Result.success();
    }

    /**
     * 数据变动时提交至队列
     */
    public Result pusherDatas(JSONArray jsonArray){
        List<SuppliersBasicInfo> list=new ArrayList<>();
        JsonUtils.convert(jsonArray);
        for (int i=0;i<jsonArray.size();i++){
            list.add(JSON.parseObject(JSON.toJSONString(jsonArray.getJSONObject(i)),SuppliersBasicInfo.class));
        }
        List<String> codes=list.stream().map(SuppliersBasicInfo::getCompanyCode).collect(Collectors.toList());
        List<SuppliersBasicInfo> list2 = this.list(new QueryWrapper<SuppliersBasicInfo>().lambda().in(SuppliersBasicInfo::getCompanyCode, codes));
        if(CollectionUtils.isEmpty(list2)){
            return Result.error(Result.Status.ERROR);
        }
        mqSupport.sendSyncMessage(AsyncConstant.TOPIC_SUPPLIERS_BASIC_INFO, JSON.toJSONString(list2, SerializerFeature.DisableCircularReferenceDetect));
        return Result.success();
    }
    public Result pusherData(JSONObject base){
        List<SuppliersBasicInfo>  list = new ArrayList<>();
        try {
            list= this.list(MyQueryWrapper.toAbstractWrapper(base,SuppliersBasicInfo.class,false));
        } catch (Exception e) {
            log.error("SuppliersBasicInfo pusherData: ",e);
        }
        if(CollectionUtils.isEmpty(list)){
            return Result.error(Result.Status.ERROR);
        }
        mqSupport.sendSyncMessage(base,AsyncConstant.TOPIC_SUPPLIERS_BASIC_INFO, JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect));

        return Result.success();
    }

    public List<CustomerSupplierVendorDTO> convertToContractData(List<SuppliersBasicInfo> suppliersBasicInfoList) {
        if (CollectionUtils.isEmpty(suppliersBasicInfoList)) {
            return Collections.emptyList();
        }
        List<CustomerSupplierVendorDTO> contractDataList = new ArrayList<>();
        // 取出所有的供应商的companyCode
        List<String> companyCodeList = suppliersBasicInfoList.stream()
                .map(SuppliersBasicInfo::getCompanyCode)
                .collect(Collectors.toList());
        // 根据companyCode查询供应商的地址信息
        List<SuppliersAddress> suppliersAddressList = suppliersAddressService.list(new QueryWrapper<SuppliersAddress>().lambda()
                .in(SuppliersAddress::getCompanyCode, companyCodeList));
        List<SuppliersBasicInfo> basicInfoToRemove = new ArrayList<>();
        for (SuppliersBasicInfo basicInfo : suppliersBasicInfoList) {
            // 在 suppliersAddressList 中查找与当前 basicInfo 的 companyCode 对应的 SuppliersAddress
            SuppliersAddress matchingAddress = suppliersAddressList.stream()
                    .filter(address -> address.getCompanyCode().equals(basicInfo.getCompanyCode()))
                    .findFirst()
                    .orElse(null);

            // 如果找到匹配的地址信息，将其 countryCode 放入 ext 字段
            if (matchingAddress != null) {
                basicInfo.setExt(matchingAddress.getRegistCountry());
            } else {
                // 如果没有找到匹配的地址信息，将该basicInfo移除
                basicInfoToRemove.add(basicInfo);
            }
        }
        suppliersBasicInfoList.removeAll(basicInfoToRemove);

        if (CollectionUtils.isNotEmpty(suppliersBasicInfoList)) {
            contractDataList = suppliersBasicInfoList.stream()
                    .map(SupplierVendorMapper.INSTANCE::supplierToCustomerSupplierVendorDTO)
                    .collect(Collectors.toList());
        }

        return contractDataList;
    }

    /**
     *合并接口+内部供应商
     */

    public JSONObject searchALL(JSONObject jsonObject) throws Exception {
        JSONObject jo=new JSONObject();
        JSONArray suppliersBasicInfosJsonArray = new JSONArray();
        List<SuppliersBasicInfo> suppliersBasicInfos = this.list(MyQueryWrapper.toAbstractWrapper(jsonObject, SuppliersBasicInfo.class, true));
        suppliersBasicInfosJsonArray = suppliersRaleInit(suppliersBasicInfos);
        jo.put("SuppliersAllInfos",suppliersBasicInfosJsonArray);
        long total=this.count(MyQueryWrapper.toAbstractWrapper(jsonObject,SuppliersBasicInfo.class,false));
        return MyQueryWrapper.toTotalList(total,jo);
    }
    /**
     * 查询内部供应商
     */
    public JSONArray searchInternalSuppliers(List<InternalSuppliers> list){
        JSONArray jsonArray = new JSONArray();
        if (!org.apache.commons.collections.CollectionUtils.isEmpty(list)){
            list.forEach(o->{
                JSONObject object = JSON.parseObject(JSON.toJSONString(o, SerializerFeature.WriteMapNullValue));
                jsonArray.add(object);
            });
        }
        return jsonArray;
    }
    /**
     * 初始化关联查询
     */
    private JSONArray suppliersRaleInit(List<SuppliersBasicInfo> suppliersBasicInfos) {
        JSONArray jsonArray=new JSONArray();
        if(!org.springframework.util.CollectionUtils.isEmpty(suppliersBasicInfos)) {
            List<String> companyCodes = suppliersBasicInfos.stream().map(SuppliersBasicInfo::getCompanyCode).collect(Collectors.toList());
            List<SuppliersAddress> suppliersAddresses = suppliersAddressService.list(new QueryWrapper<SuppliersAddress>().lambda().in(SuppliersAddress::getCompanyCode, companyCodes));
            List<SuppliersContact> suppliersContacts = suppliersContactService.list(new QueryWrapper<SuppliersContact>().lambda().in(SuppliersContact::getCompanyCode, companyCodes));
            List<SuppliersFinancial> suppliersFinancials = suppliersFinancialService.list(new QueryWrapper<SuppliersFinancial>().lambda().in(SuppliersFinancial::getCompanyCode, companyCodes));
            List<SuppliersBankAccount> suppliersBankAccounts = suppliersBankAccountService.list(new QueryWrapper<SuppliersBankAccount>().lambda().in(SuppliersBankAccount::getCompanyCode, companyCodes));
            List<SuppliersFactory> suppliersFactories = suppliersFactoryService.list(new QueryWrapper<SuppliersFactory>().lambda().in(SuppliersFactory::getCompanyCode, companyCodes));

            for(SuppliersBasicInfo o : suppliersBasicInfos){
                JSONObject suppliersBasicInfo =JSON.parseObject(JSON.toJSONString(o));

                JSONObject suppliersAddressObject=new JSONObject();
                if(CollectionUtils.isNotEmpty(suppliersAddresses)) {
                    List<SuppliersAddress> sList=suppliersAddresses.stream().filter(s -> s.getCompanyCode().equals(o.getCompanyCode())).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(sList)) {
                        SuppliersAddress suppliersAddressInfo = suppliersAddresses.stream().filter(s -> s.getCompanyCode().equals(o.getCompanyCode())).collect(Collectors.toList()).get(0);
                        if (suppliersAddressInfo != null) {
                            suppliersAddressObject = JSON.parseObject(JSON.toJSONString(suppliersAddressInfo, SerializerFeature.WriteMapNullValue));
                            suppliersAddressObject.remove("companyName");
                        }
                    }
                }
                suppliersBasicInfo.put("suppliersAddressInfo", suppliersAddressObject);

                JSONArray suppliersContactObject=new JSONArray();
                if (CollectionUtils.isNotEmpty(suppliersContacts)) {
                    List<SuppliersContact> suppliersContactInfos = suppliersContacts.stream().filter(s -> s.getCompanyCode().equals(o.getCompanyCode())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(suppliersContactInfos)) {
                        suppliersContactInfos.forEach(suppliersContact->{
                            JSONObject object = JSON.parseObject(JSON.toJSONString(suppliersContact, SerializerFeature.WriteMapNullValue));
                            object.remove("companyName");
                            suppliersContactObject.add(object);
                        });
                        //suppliersContactObject = JSONArray.parseArray(JSON.toJSONString(suppliersContactInfos, SerializerFeature.WriteMapNullValue));
                    }
                }
                suppliersBasicInfo.put("suppliersContactInfos", suppliersContactObject);

                JSONArray suppliersFinancialObject=new JSONArray();
                if(CollectionUtils.isNotEmpty(suppliersFinancials)) {
                    List<SuppliersFinancial> suppliersFinancialInfos = suppliersFinancials.stream().filter(s -> s.getCompanyCode().equals(o.getCompanyCode())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(suppliersFinancialInfos)) {
                        suppliersFinancialInfos.forEach(suppliersFinancial->{
                            JSONObject object = JSON.parseObject(JSON.toJSONString(suppliersFinancial, SerializerFeature.WriteMapNullValue));
                            object.remove("companyName");
                            object.remove("pdtName");
                            suppliersFinancialObject.add(object);
                        });
//                        suppliersFinancialObject = JSONArray.parseArray(JSON.toJSONString(suppliersFinancialInfos, SerializerFeature.WriteMapNullValue));
                    }
                }
                suppliersBasicInfo.put("suppliersFinancialInfos", suppliersFinancialObject);

                JSONArray suppliersBankAccountObject=new JSONArray();
                if(CollectionUtils.isNotEmpty(suppliersBankAccounts)) {
                    List<SuppliersBankAccount> suppliersBankAccountInfos = suppliersBankAccounts.stream().filter(s -> s.getCompanyCode().equals(o.getCompanyCode())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(suppliersBankAccountInfos)) {
                        suppliersBankAccountInfos.forEach(suppliersBankAccount->{
                            JSONObject object = JSON.parseObject(JSON.toJSONString(suppliersBankAccount, SerializerFeature.WriteMapNullValue));
                            object.remove("companyName");
                            suppliersBankAccountObject.add(object);
                        });
//                        suppliersBankAccountObject = JSONArray.parseArray(JSON.toJSONString(suppliersBankAccountInfos, SerializerFeature.WriteMapNullValue));
                    }
                }
                suppliersBasicInfo.put("suppliersBankAccountInfos", suppliersBankAccountObject);

                JSONArray suppliersFactoryObject = new JSONArray();
                if(CollectionUtils.isNotEmpty(suppliersFactories)) {
                    List<SuppliersFactory> suppliersFactoryInfos = suppliersFactories.stream().filter(s -> s.getCompanyCode().equals(o.getCompanyCode())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(suppliersFactoryInfos)) {
                        suppliersFactoryInfos.forEach(suppliersFactory->{
                            JSONObject object = JSON.parseObject(JSON.toJSONString(suppliersFactory, SerializerFeature.WriteMapNullValue));
                            object.remove("companyName");
                            suppliersFactoryObject.add(object);
                        });
//                        suppliersFactoryObject = JSONArray.parseArray(JSON.toJSONString(suppliersFactoryInfos, SerializerFeature.WriteMapNullValue));
                    }
                }
                suppliersBasicInfo.put("suppliersFactoryInfos", suppliersFactoryObject);
                suppliersBasicInfo.remove("parentCompanyName");
                jsonArray.add(suppliersBasicInfo);
            }
        }
        return jsonArray;
    }
    /**
     * 同步
     */
    @Transactional(rollbackFor = Exception.class)
    public Result saveAll(JSONArray jsonArray){
        if(CollectionUtils.isEmpty(jsonArray)){
            return Result.error("参数不能为空");
        }
        try {
            return toSuppliers(jsonArray);
        }catch (Exception e){
            log.error("toSuppliers: ",e);
            return Result.error(e.getMessage());
        }
    }
    private Result toSuppliers(JSONArray jsonArray){
        JSONArray suppliersContactJson=new JSONArray();
        JSONArray suppliersFinancialJson=new JSONArray();
        JSONArray suppliersBankAccountJson=new JSONArray();
        JSONArray suppliersFactoryJson=new JSONArray();
        JSONArray suppliersAddressJson=new JSONArray();
        JSONArray suppliersCustomerRelaJson=new JSONArray();
        JSONArray customerSupplierRelaJson = new JSONArray();

        for (int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            String companyCode=jsonObject.getString("companyCode");
            if(jsonObject.getJSONObject("suppliersAddressInfo")!=null){
                suppliersAddressJson.add(jsonObject.getJSONObject("suppliersAddressInfo"));
            }
            if(jsonObject.getJSONArray("suppliersContactInfos")!=null) {
                suppliersContactJson.addAll(jsonObject.getJSONArray("suppliersContactInfos"));
            }
            if(jsonObject.getJSONArray("suppliersFinancialInfos")!=null) {
                suppliersFinancialJson.addAll(jsonObject.getJSONArray("suppliersFinancialInfos"));
            }
            if(jsonObject.getJSONArray("suppliersBankAccountInfos")!=null) {
                suppliersBankAccountJson.addAll(jsonObject.getJSONArray("suppliersBankAccountInfos"));
            }
            if(jsonObject.getJSONArray("suppliersFactoryInfos")!=null) {
                suppliersFactoryJson.addAll(jsonObject.getJSONArray("suppliersFactoryInfos"));
            }
            // 新增子对象一对多, 上游只传id
            customerSupplierRelaJson=jsonObject.getJSONArray("customerSupplierRela");
            if(customerSupplierRelaJson!=null&&customerSupplierRelaJson.size()>0){
                for(Object o : customerSupplierRelaJson){
                    JSONObject object = JSON.parseObject(JSON.toJSONString(o));
                    object.put("companyCode",jsonObject.getString("companyCode"));
                    suppliersCustomerRelaJson.add(object);
                }
            }
            // 删除在数据库关联的子对象
            suppliersAddressService.remove(new QueryWrapper<SuppliersAddress>().lambda().eq(SuppliersAddress::getCompanyCode, companyCode));
            // 删除在数据库关联的子对象
            suppliersContactService.remove(new QueryWrapper<SuppliersContact>().lambda().eq(SuppliersContact::getCompanyCode, companyCode));
            // 删除在数据库关联的子对象
            suppliersFinancialService.remove(new QueryWrapper<SuppliersFinancial>().lambda().eq(SuppliersFinancial::getCompanyCode, companyCode));
            // 删除在数据库关联的子对象
            suppliersBankAccountService.remove(new QueryWrapper<SuppliersBankAccount>().lambda().eq(SuppliersBankAccount::getCompanyCode, companyCode));
            // 删除在数据库关联的子对象
            suppliersFactoryService.remove(new QueryWrapper<SuppliersFactory>().lambda().eq(SuppliersFactory::getCompanyCode, companyCode));
            // 删除在数据库关联的子对象
            customerSupplierRelaService.remove(new QueryWrapper<CustomerSupplierRela>().lambda().eq(CustomerSupplierRela::getCompanyCode, companyCode));
        }
        Result result=new Result();
        if(suppliersAddressJson!=null&&suppliersAddressJson.size()>0){
            result = suppliersAddressService.save(suppliersAddressJson);
            if (result.isHasErrors()){
                return result;
            }
        }
        if(suppliersContactJson!=null&&suppliersContactJson.size()>0){
            result = suppliersContactService.save(suppliersContactJson);
            if (result.isHasErrors()){
                return result;
            }
        }
        if (suppliersFinancialJson!=null&&suppliersFinancialJson.size()>0){
            result = suppliersFinancialService.save(suppliersFinancialJson);
            if (result.isHasErrors()){
                return result;
            }
        }
        if (suppliersBankAccountJson!=null&&suppliersBankAccountJson.size()>0) {
            result = suppliersBankAccountService.save(suppliersBankAccountJson);
            if (result.isHasErrors()) {
                return result;
            }
        }
        if (suppliersFactoryJson!=null&&suppliersFactoryJson.size()>0) {
            result = suppliersFactoryService.save(suppliersFactoryJson);
            if (result.isHasErrors()) {
                return result;
            }
        }
        if (jsonArray!=null) {
            result = this.toSuppliersBasicInfo(jsonArray);
            if (result.isHasErrors()) {
                return result;
            }
        }
        if(suppliersCustomerRelaJson!=null && suppliersCustomerRelaJson.size()>0){
            log.info("客商关系同步");
            result=customerSupplierRelaService.save(suppliersCustomerRelaJson);
            if (result.isHasErrors()) {
                return result;
            }
            try {//提交队列
                result = customerSupplierRelaService.pusherDatas(suppliersCustomerRelaJson);
            }catch (Exception e) {
                return Result.error("mq异常："+e.getMessage());
            }
        }
        return result;
    }

    /**
     * 消息队列
     */
    public Result pusherAllDatas(JSONArray jsonArray){
        List<SuppliersBasicInfo> list=new ArrayList<>();
        JsonUtils.convert(jsonArray);
        for (int i=0;i<jsonArray.size();i++){
            list.add(JSON.parseObject(JSON.toJSONString(jsonArray.getJSONObject(i)),SuppliersBasicInfo.class));
        }
        List<String> codes=list.stream().map(SuppliersBasicInfo::getCompanyCode).collect(Collectors.toList());
        List<SuppliersBasicInfo> list2 = this.list(new QueryWrapper<SuppliersBasicInfo>().lambda().in(SuppliersBasicInfo::getCompanyCode, codes));
        jsonArray=suppliersRaleInit(list2);
        if(CollectionUtils.isEmpty(list2)){
            return Result.error(Result.Status.ERROR);
        }
        mqSupport.sendSyncMessage(AsyncConstant.TOPIC_SUPPLIERS_ALL_INFO, JSON.toJSONString(jsonArray, SerializerFeature.DisableCircularReferenceDetect));
        return Result.success();
    }

    /**
     * 调消息队列时，会发送供应商
     * @param base
     * @return
     */
    public Result pusherAllData(JSONObject base){
//        String type=base.getString("type");
        List<SuppliersBasicInfo>  list = new ArrayList<>();
        JSONArray sAllJsonArray=new JSONArray();
        try {
            list= this.list(MyQueryWrapper.toAbstractWrapper(base,SuppliersBasicInfo.class,true));
            sAllJsonArray=suppliersRaleInit(list);
        } catch (Exception e) {
            log.error("供应商 pusherAllData: ",e);
        }
        if(CollectionUtils.isEmpty(sAllJsonArray)){
            return Result.error(Result.Status.ERROR);
        }
        mqSupport.sendSyncMessage(base,AsyncConstant.TOPIC_SUPPLIERS_ALL_INFO, JSON.toJSONString(sAllJsonArray, SerializerFeature.DisableCircularReferenceDetect));
        return Result.success();
    }

    public Result pushContractData(JSONObject base){
        List<SuppliersBasicInfo> list = new ArrayList<>();
        try {
            list = this.list(MyQueryWrapper.toAbstractWrapper(base, SuppliersBasicInfo.class, true));
        } catch (Exception e) {
            log.error("SuppliersBasicInfo pushContractData: ", e);
        }
        if (CollectionUtils.isEmpty(list)) {
            return Result.error(Result.Status.ERROR);
        }
        List<CustomerSupplierVendorDTO> contractDataList = convertToContractData(list);
        for (CustomerSupplierVendorDTO customerSupplierVendorDTO : contractDataList) {
            mqSupport.sendSyncMessage(base, AsyncConstant.TOPIC_SUPPLIERS_BASIC_CONTACT_INFO, customerSupplierVendorDTO);
        }
        return Result.success();

    }
    @Override
    public boolean save(SuppliersBasicInfo entity) {
        boolean success = super.save(entity);
        return ContractPersistenceUtil.executeSaveOperation(success, entity, suppliersBasicInfoPersistenceListener);
    }

    @Override
    public boolean updateById(SuppliersBasicInfo entity) {
        boolean success = super.updateById(entity);
        return ContractPersistenceUtil.executeSaveOperation(success, entity, suppliersBasicInfoPersistenceListener);
    }
}


package com.anker.mdp.service;

        import com.alibaba.fastjson.JSON;
        import com.alibaba.fastjson.JSONArray;
        import com.alibaba.fastjson.JSONObject;
        import com.alibaba.fastjson.serializer.SerializerFeature;
        import com.anker.mdp.Listener.SuppliersBasicInfoPersistenceListener;
        import com.anker.mdp.constant.AsyncConstant;
        import com.anker.mdp.entity.*;
        import com.anker.mdp.mapper.SuppliersAddressMapper;
        import com.anker.mdp.mq.MQSupport;
        import com.anker.mdp.util.ContractPersistenceUtil;
        import com.anker.mdp.util.JsonUtils;
        import com.anker.mdp.util.MyQueryWrapper;
        import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
        import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
        import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import java.util.*;
        import java.util.function.Function;
        import java.util.stream.Collectors;


/**
 * <p>
 *   服务实现类
 * </p>
 *
 * @author Mybatis Plus Code Generator
 * @since 2022-09-05
 */
@Service
public class SuppliersAddressService extends ServiceImpl<SuppliersAddressMapper, SuppliersAddress> {
    @Autowired
    private MQSupport mqSupport;
    @Autowired
    private CountryService countryService;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private CityService cityService;
    @Autowired
    private SuppliersBasicInfoPersistenceListener suppliersBasicInfoPersistenceListener;
    @Autowired
    private SuppliersBasicInfoService suppliersBasicInfoService;

    public JSONObject search(JSONObject jsonObject) throws Exception {
        List<SuppliersAddress> suppliersAddresses=this.list(MyQueryWrapper.toAbstractWrapper(jsonObject,SuppliersAddress.class,true));
        long total=this.count(MyQueryWrapper.toAbstractWrapper(jsonObject,SuppliersAddress.class,false));
        JSONArray jsonArray=new JSONArray();
        if(!org.apache.commons.collections.CollectionUtils.isEmpty(suppliersAddresses)) {
            suppliersAddresses.forEach(o -> {
                JSONObject object = JSON.parseObject(JSON.toJSONString(o));
                jsonArray.add(object);
            });
        }
        return MyQueryWrapper.toTotalList(total,jsonArray);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result save(JSONArray jsonArray){
        if(CollectionUtils.isEmpty(jsonArray)){
            return Result.error("参数不能为空");
        }
        try {
            return toSuppliersAddress(jsonArray);
        }catch (Exception e){
            log.error("toSuppliersAddress: ",e);
            return Result.error(e.getMessage());
        }
    }

    private Result toSuppliersAddress(JSONArray jsonArray) throws Exception {
        List<SuppliersAddress> list=new ArrayList<>();
        JsonUtils.convert(jsonArray);
        for (int i=0;i<jsonArray.size();i++){
            SuppliersAddress suppliersAddress=JSON.toJavaObject(jsonArray.getJSONObject(i),SuppliersAddress.class);
            list.add(suppliersAddress);
        }
        List<String> codes = new ArrayList<>();
        List<String> registCountrys=new ArrayList<>();
        List<String> registProvince=new ArrayList<>();
        List<String> registCity=new ArrayList<>();
        for (SuppliersAddress s:list){
            codes.add(s.getCompanyCode());
            registCountrys.add(s.getRegistCountry());
            registProvince.add(s.getRegistProvince());
            registCity.add(s.getRegistCity());
        }

        Map<String,Country> countryMap=new HashMap<>();
        if(CollectionUtils.isNotEmpty(registCountrys)) {
            List<Country> list2 = countryService.list(new QueryWrapper<Country>().lambda().in(Country::getAbbreviation, registCountrys));
            countryMap=list2.stream().collect(Collectors.toMap(Country::getAbbreviation, Function.identity(), (v1, v2)->v1));
        }

        Map<String,Province> provinceMap=new HashMap<>();
        if(CollectionUtils.isNotEmpty(registProvince)) {
            List<Province> provinceList = provinceService.list(new QueryWrapper<Province>().lambda().in(Province::getCnName, registProvince));
            provinceMap=provinceList.stream().collect(Collectors.toMap(Province::getCnName,Function.identity(),(v1,v2)->v1));
        }

        List<City> cityList=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(registCity)) {
            cityList = cityService.list(new QueryWrapper<City>().lambda().in(City::getCnName, registCity));
        }

        for(int i=0;i<list.size();i++){
            SuppliersAddress o=list.get(i);
            Country country=countryMap.get(o.getRegistCountry());
            Province province=provinceMap.get(o.getRegistProvince());
            City city=new City();
            if (province!=null&&o.getRegistCity()!=null) {
                List<City> cityList1=cityList.stream().filter(c -> o.getRegistCity().equals(c.getCnName()) && o.getRegistProvince().equals(province.getCnName())).collect(Collectors.toList());
                if (cityList1!=null&&cityList1.size()>0){
                    city=cityList1.get(0);
                }
            }
            if (country!=null){
                o.setCountryCode(country.getCode());
            }else{
                o.setCountryCode("");
            }
            if (city!=null){
                o.setCityCode(city.getCode());
                o.setProvinceCode(city.getProvinceCode());
            }else{
                o.setCityCode("");
                o.setProvinceCode("");
            }
            o.setId(null);
            this.save(o);
        }



        return Result.success();
    }
    /**
     * 数据变动时提交至队列
     */
    public Result pusherDatas(JSONArray jsonArray){
        List<SuppliersAddress> list=new ArrayList<>();
        JsonUtils.convert(jsonArray);
        for (int i=0;i<jsonArray.size();i++){
            list.add(JSON.parseObject(JSON.toJSONString(jsonArray.getJSONObject(i)),SuppliersAddress.class));
        }
        List<String> codes=list.stream().map(SuppliersAddress::getCompanyCode).collect(Collectors.toList());
        List<SuppliersAddress> list2 = this.list(new QueryWrapper<SuppliersAddress>().lambda().in(SuppliersAddress::getCompanyCode, codes));
        if(CollectionUtils.isEmpty(list2)){
            return Result.error(Result.Status.ERROR);
        }

        mqSupport.sendSyncMessage(AsyncConstant.TOPIC_SUPPLIERS_ADDRESS, JSON.toJSONString(list2, SerializerFeature.DisableCircularReferenceDetect));
        return Result.success();
    }
    public Result pusherData(JSONObject base){
        List<SuppliersAddress>  list = new ArrayList<>();
        try {
            list= this.list(MyQueryWrapper.toAbstractWrapper(base,SuppliersAddress.class,false));
        } catch (Exception e) {
            log.error("SuppliersAddress pusherData: ",e);
        }
        if(CollectionUtils.isEmpty(list)){
            return Result.error(Result.Status.ERROR);
        }

        mqSupport.sendSyncMessage(AsyncConstant.TOPIC_SUPPLIERS_ADDRESS, JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect));

        return Result.success();
    }

    @Override
    public boolean save(SuppliersAddress entity) {
        boolean success = super.save(entity);

        SuppliersBasicInfo one = suppliersBasicInfoService.getOne(
                new QueryWrapper<SuppliersBasicInfo>().lambda()
                        .eq(SuppliersBasicInfo::getCompanyCode, entity.getCompanyCode()));
        return ContractPersistenceUtil.executeSaveOperation(success, one, suppliersBasicInfoPersistenceListener);
    }
}
