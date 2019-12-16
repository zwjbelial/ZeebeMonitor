package io.zeebe.monitor.dao.Mapper;

import io.zeebe.monitor.bean.common.ZeebeResultCommonBean;
import org.springframework.stereotype.Repository;

@Repository
public interface ZeebeResultCommonBeanMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ZeebeResultCommonBean record);

    int insertSelective(ZeebeResultCommonBean record);

    ZeebeResultCommonBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ZeebeResultCommonBean record);

    int updateByPrimaryKeyWithBLOBs(ZeebeResultCommonBean record);

    int updateByPrimaryKey(ZeebeResultCommonBean record);
}