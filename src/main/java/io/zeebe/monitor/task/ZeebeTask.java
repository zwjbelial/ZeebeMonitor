package io.zeebe.monitor.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时器
 */
@Service
public class ZeebeTask {

    private static final Logger LOG = LoggerFactory.getLogger(ZeebeTask.class);

    @Autowired
    ZeebeTaskService zeebeTaskService;

    /**
     * 1分钟一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void executeOneMinutes() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LOG.info("每分钟监控启动 : '{}'", sdf.format(new Date()));
            zeebeTaskService.doZeebeMonitor();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
