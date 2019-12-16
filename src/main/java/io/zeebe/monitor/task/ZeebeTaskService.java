package io.zeebe.monitor.task;

import io.zeebe.monitor.bean.ZeebeMonitorListBean;
import io.zeebe.monitor.bean.ZeebeResultBean;
import io.zeebe.monitor.bean.common.ZeebeResultCommonBean;
import io.zeebe.monitor.dao.ZeebeMonitorListDao;
import io.zeebe.monitor.dao.ZeebeResultDao;
import io.zeebe.monitor.entity.WorkflowInstanceEntity;
import io.zeebe.monitor.repository.WorkflowInstanceRepository;
import io.zeebe.monitor.rest.JobDto;
import io.zeebe.monitor.rest.ViewController;
import io.zeebe.monitor.rest.WorkflowInstanceDto;
import io.zeebe.monitor.rest.WorkflowInstanceListDto;
import io.zeebe.monitor.utils.TimeUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static io.zeebe.monitor.utils.NumberUtils.saveTwoFormat;
import static io.zeebe.monitor.utils.TimeUtils.timeFormatTurn;

@Service
public class ZeebeTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(ZeebeTaskService.class);

    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Autowired
    private ViewController viewController;

    @Autowired
    private ZeebeResultDao zeebeResultDao;

    @Autowired
    private ZeebeMonitorListDao zeebeMonitorListDao;

    //jobs的状态集合
    //created : 创建
    //failed : 失败
    //actived : 激活
    //time_out : 超时
    //retries_updated : 重试
    //completed : 完成
    private String[] stateArr = {"created", "failed", "activated", "timed_out", "retries_updated"};

    /**
     * zeebe监控程序入口
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> doZeebeMonitor() throws Exception {
        //返回的Map
        Map<String, Object> resultMap = new HashMap<>();
        try {
            /************************************************** 监控准备数据 START **************************************************/
            //数据库中job监控标准
            List<ZeebeMonitorListBean> zeebeMonitorListBeans = zeebeMonitorListDao.getAllZeebeMonitorInfo();
            //对job监控标准进行数据处理
            Map<String, ZeebeMonitorListBean> zeebeMonitorListBeanMap = dealZeebeMonitorListBeans(zeebeMonitorListBeans);

            //获取所有的实例
            List<WorkflowInstanceListDto> instancesList = getInstanceList();

            //获取mysql数据库中的所有数据的jobKey
            List<Long> jobKeyInMysqlList = zeebeResultDao.getAllJobKeys();

            //存放整体结果列表
            List<ZeebeResultBean> resultBeanList = new ArrayList<>();
            //需要更新的数据List
            List<ZeebeResultBean> updateBean = new ArrayList<>();
            //需要插入的数据List
            List<ZeebeResultBean> insertBean = new ArrayList<>();
            //插入err_log表
            List<ZeebeResultBean> errLogBean = new ArrayList<>();
            /************************************************** 监控准备数据 END **************************************************/

            //instance为空的时候,说明根本没有启动实例
            if (instancesList == null || instancesList.size() == 0) {
                //这里可能是日更新完成了,所有没有实例
                LOG.warn("======================================== 未发现实例 ========================================");
            } else {
                //遍历instance列表,按道理只应该有一个,即只有一个日更新实例在运行
                for (WorkflowInstanceListDto instance : instancesList) {
                    //获取实例的key
                    long workflowInstanceKey = instance.getWorkflowInstanceKey();

                    //实例的状态
                    String instanceState = instance.getState();

                    //不进行校验的实例状态:完成的实例和被杀掉的实例不进行校验
                    String[] instanceStatesNotUsed = {"Completed", "Terminated", "Canceled"};

                    // 已经完成/取消的实例,不进行校验,后边会先把状态置成正确,然后更新错误的状态,所以这里不进行校验
                    if (Arrays.asList(instanceStatesNotUsed).contains(instanceState)) {
                        continue;
                    }

                    //根据实例的key,调用获取里边的job数据,这里复用monitor原来的方法
                    WorkflowInstanceEntity instanceEntity = workflowInstanceRepository.findByKey(workflowInstanceKey).orElse(null);
                    WorkflowInstanceDto instanceDto = viewController.toInstanceDto(instanceEntity);

                    //当前实例的job列表
                    List<JobDto> jobs = instanceDto.getJobs();

                    //临时变量
                    ZeebeResultBean tempBean;

                    //遍历所有job
                    for (JobDto tempDto : jobs) {
                        //初始化结果Bean的基础信息
                        tempBean = initZeebeResult(tempDto);

                        //校验条件:retries == 0 && state == failed
                        int retries = tempBean.getRetries();
                        String state = tempBean.getState();
                        String jobType = tempBean.getJobType();
                        //job运行时间范围:默认为5小时,防止没有填写的
                        int runTime = 5 * 60;
                        //job展示名称
                        String jobName = "未命名";

                        //数据库中的校验标准
                        ZeebeMonitorListBean zeebeMonitorBean = zeebeMonitorListBeanMap.get(jobType);

                        if (zeebeMonitorBean == null) {
                            LOG.warn("==================== 未发现校验时间内容,请及时补全信息 ====================");
                        } else {
                            //需要校验,是否存在
                            runTime = zeebeMonitorBean.getRunTime();
                            jobName = zeebeMonitorBean.getJobName();
                        }

                        tempBean.setJobName(jobName);

                        //校验是否为failed && 剩余重试次数为0
                        if ("failed".equals(state) && retries == 0) {
                            tempBean.setErrorFlag("1");
                            tempBean.setMessage("job failed :" + jobName + "(" + tempBean.getJobType() + ") 异常");
//                            LOG.info(tempBean.getMessage());
                        } else if (Arrays.asList("created", "activated", "timed_out", "retries_updated").contains(state)) {
                            //校验时间

                            //现在时间的毫秒值
                            Long nowTime = new Date().getTime();
                            //job开始时间的毫秒值
                            Long jobStartTime = tempBean.getStartDate().getTime();

                            Long runTimeRange = runTime * 60 * 1000L;

                            Long timeUsed = nowTime - jobStartTime;

                            if (timeUsed > runTimeRange) {
                                //超时了
                                tempBean.setMessage("job overtime ：" + jobName + "(" + tempBean.getJobType() + ") 超时，当前状态 ： " + state + "，开始时间 ： " + timeFormatTurn(tempBean.getStartDate())
                                        + "，监控时间 ： " + timeFormatTurn(new Date()) + "，规定时间 ： " + runTime + "分钟" + "，已用时间 ： " + saveTwoFormat(timeUsed / (60 * 1000D)) + "分钟");
                                tempBean.setErrorFlag("1");
//                                LOG.info(tempBean.getMessage());
                            } else {
                                tempBean.setMessage("job " + state + " ： " + jobName + "(" + tempBean.getJobType() + ") is " + state);
                                tempBean.setErrorFlag("0");
//                                LOG.info(tempBean.getMessage());
                            }
                        } else {
                            tempBean.setMessage("job " + state + " ： " + jobName + "(" + tempBean.getJobType() + ") is " + state);
                            tempBean.setErrorFlag("0");
//                            LOG.info(tempBean.getMessage());
                        }

                        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
                        tempBean.setUpdater(methodName);
                        //判断是否再结果表中已经存在,存在则放入更新list,位置放入插入list,最后统一进行更新和插入
                        if (jobKeyInMysqlList.contains(tempBean.getJobKey())) {
                            updateBean.add(tempBean);
                            LOG.info("updateBean : " + tempBean.getJobName() + " | " + tempBean.getJobKey() + " | " + tempBean.getState());
                        } else {
                            insertBean.add(tempBean);
                            LOG.info("insertBean : " + tempBean.getJobName() + " | " + tempBean.getJobKey() + " | " + tempBean.getState());
                        }

                        //将错误的数据插入到错误结果日志表,方便以后复查
                        if ("1".equals(tempBean.getErrorFlag())) {
                            errLogBean.add(tempBean);
                        }

                        resultBeanList.add(tempBean);
                    }
                }
            }

            //将所有结果表数据更新状态为正确
            zeebeResultDao.updateAllErrFlagRight();
            if (updateBean.size() > 0) {
                //批量更新
                zeebeResultDao.updateZeebeResultBatchByJobKey(updateBean);
            }
            if (insertBean.size() > 0) {
                //批量插入
                zeebeResultDao.insertZeebeResultBatch(insertBean);
            }
            if (errLogBean.size() > 0) {
                //错误日志表批量插入
                zeebeResultDao.insertZeebeResultErrLogBatch(errLogBean);
            }

            resultMap.put("resultBeanList", resultBeanList);
            resultMap.put("status", 0);
        } catch (Exception e) {
            resultMap.put("status", 1);
            resultMap.put("msg", e.getMessage());
            throw e;
        }
        return resultMap;
    }

    /**
     * 处理数据库job标准
     *
     * @param zeebeMonitorListBeans
     * @return
     */
    private Map<String, ZeebeMonitorListBean> dealZeebeMonitorListBeans(List<ZeebeMonitorListBean> zeebeMonitorListBeans) {
        Map<String, ZeebeMonitorListBean> result = new HashMap<>();

        for (ZeebeMonitorListBean tempBean : zeebeMonitorListBeans) {
            result.put(tempBean.getJobType(), tempBean);
        }
        return result;
    }

    /**
     * 对结果进行赋值
     *
     * @param jobDto
     * @return
     */
    private ZeebeResultBean initZeebeResult(JobDto jobDto) {
        ZeebeResultBean zeebeResultBean = new ZeebeResultBean();

        zeebeResultBean.setJobKey(jobDto.getKey());
        zeebeResultBean.setState(jobDto.getState());
        zeebeResultBean.setJobType(jobDto.getJobType());
        zeebeResultBean.setWorker(jobDto.getWorker());
        zeebeResultBean.setRetries(jobDto.getRetries());
        zeebeResultBean.setActivityId(jobDto.getActivityId());
        zeebeResultBean.setActivityInstanceKey(jobDto.getActivityInstanceKey() + "");
        zeebeResultBean.setWorkflowInstanceKey(jobDto.getWorkflowInstanceKey() + "");
        zeebeResultBean.setStartDate(jobDto.getStartDate());

        return zeebeResultBean;
    }

    /**
     * 获取所有的instances,正常情况为只有一条数据
     *
     * @return
     */
    private List<WorkflowInstanceListDto> getInstanceList() {

        List<WorkflowInstanceListDto> instances = new ArrayList<>();

        for (WorkflowInstanceEntity instanceEntity : workflowInstanceRepository.findAll()) {
            WorkflowInstanceListDto dto = viewController.toDto(instanceEntity);
            instances.add(dto);
        }

        return instances;
    }

}
