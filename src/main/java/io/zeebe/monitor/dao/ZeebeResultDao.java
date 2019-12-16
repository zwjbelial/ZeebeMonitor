package io.zeebe.monitor.dao;

import io.zeebe.monitor.bean.ZeebeResultBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZeebeResultDao {

    //获取mysql数据库中的所有数据
    List<Long> getAllJobKeys();

    //将所有结果表数据更新状态为正确
    void updateAllErrFlagRight();

    //批量更新
    void updateZeebeResultBatchByJobKey(List<ZeebeResultBean> updateBean);

    //批量插入
    void insertZeebeResultBatch(List<ZeebeResultBean> insertBean);

    //错误日志表批量插入
    void insertZeebeResultErrLogBatch(List<ZeebeResultBean> errLogBean);
}