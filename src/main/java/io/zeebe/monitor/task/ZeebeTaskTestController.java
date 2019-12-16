package io.zeebe.monitor.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ZeebeTaskTestController {

    private static final Logger LOG = LoggerFactory.getLogger(ZeebeTaskTestController.class);

    @Autowired
    ZeebeTaskService zeebeTaskService;

    @GetMapping("/test")
    @ResponseBody
    public Map<String, Object> executeFiveMinutes() {
        Map<String, Object> result = new HashMap<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LOG.info("主动激活监控程序 : '{}'", sdf.format(new Date()));
            result = zeebeTaskService.doZeebeMonitor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
