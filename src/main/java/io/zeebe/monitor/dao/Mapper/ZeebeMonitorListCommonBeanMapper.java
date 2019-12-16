package io.zeebe.monitor.dao.Mapper;

import io.zeebe.monitor.bean.common.ZeebeMonitorListCommonBean;
import org.springframework.stereotype.Repository;

@Repository
public interface ZeebeMonitorListCommonBeanMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ZeebeMonitorListCommonBean record);

    int insertSelective(ZeebeMonitorListCommonBean record);

    ZeebeMonitorListCommonBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ZeebeMonitorListCommonBean record);

    int updateByPrimaryKey(ZeebeMonitorListCommonBean record);
}