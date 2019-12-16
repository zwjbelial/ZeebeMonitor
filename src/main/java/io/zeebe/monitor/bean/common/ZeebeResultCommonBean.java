package io.zeebe.monitor.bean.common;

import java.util.Date;

public class ZeebeResultCommonBean {
    private Integer id;

    private Long jobKey;

    private String jobName;

    private String state;

    private String jobType;

    private String worker;

    private Integer retries;

    private String activityId;

    private String activityInstanceKey;

    private String workflowInstanceKey;

    private Date startDate;

    private String errorFlag;

    private String delFlag;

    private String creator;

    private Date createTime;

    private String updater;

    private Date updateTime;

    private String message;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getJobKey() {
        return jobKey;
    }

    public void setJobKey(Long jobKey) {
        this.jobKey = jobKey;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityInstanceKey() {
        return activityInstanceKey;
    }

    public void setActivityInstanceKey(String activityInstanceKey) {
        this.activityInstanceKey = activityInstanceKey;
    }

    public String getWorkflowInstanceKey() {
        return workflowInstanceKey;
    }

    public void setWorkflowInstanceKey(String workflowInstanceKey) {
        this.workflowInstanceKey = workflowInstanceKey;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(String errorFlag) {
        this.errorFlag = errorFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", jobKey=").append(jobKey);
        sb.append(", jobName=").append(jobName);
        sb.append(", state=").append(state);
        sb.append(", jobType=").append(jobType);
        sb.append(", worker=").append(worker);
        sb.append(", retries=").append(retries);
        sb.append(", activityId=").append(activityId);
        sb.append(", activityInstanceKey=").append(activityInstanceKey);
        sb.append(", workflowInstanceKey=").append(workflowInstanceKey);
        sb.append(", startDate=").append(startDate);
        sb.append(", errorFlag=").append(errorFlag);
        sb.append(", delFlag=").append(delFlag);
        sb.append(", creator=").append(creator);
        sb.append(", createTime=").append(createTime);
        sb.append(", updater=").append(updater);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", message=").append(message);
        sb.append("]");
        return sb.toString();
    }
}