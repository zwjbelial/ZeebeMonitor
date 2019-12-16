package io.zeebe.monitor.dao;

import io.zeebe.monitor.bean.ZeebeMonitorListBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZeebeMonitorListDao {

    //数据库中job相关数据
    List<ZeebeMonitorListBean> getAllZeebeMonitorInfo();
}