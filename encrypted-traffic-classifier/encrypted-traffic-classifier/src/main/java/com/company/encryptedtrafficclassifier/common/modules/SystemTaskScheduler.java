package com.company.encryptedtrafficclassifier.common.modules;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.company.encryptedtrafficclassifier.common.enums.SystemCode;
import com.company.encryptedtrafficclassifier.entity.Result;
import com.company.encryptedtrafficclassifier.entity.Task;
import com.company.encryptedtrafficclassifier.service.ResultService;
import com.company.encryptedtrafficclassifier.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class SystemTaskScheduler {

    public static final BlockingQueue<Task> preproccessTaskQueue = new LinkedBlockingQueue<>();

    public static final BlockingQueue<Task> classifyTaskQueue = new LinkedBlockingQueue<>();

    @Autowired
    TaskService taskService;

    @Autowired
    ResultService resultService;

    @Scheduled(fixedRate = 5 * 1000)
    public void displayQueues() {
        List<Long> preproccessTaskIdList = new ArrayList<>();
        for (Task task : preproccessTaskQueue) {
            preproccessTaskIdList.add(task.getId());
        }
        List<Long> classifyTaskIdList = new ArrayList<>();
        for (Task task : classifyTaskQueue) {
            classifyTaskIdList.add(task.getId());
        }
        log.info("数据预处理队列长度：" + preproccessTaskQueue.size() + "，" + preproccessTaskIdList.toString());
        log.info("流量分类队列长度：" + classifyTaskQueue.size() + "，" + classifyTaskIdList.toString());
    }

    @Scheduled(fixedDelay = 10 * 1000)
    public void scheduleTasks() {
        List<Task> unpreproccessedTaskList = taskService.list(new QueryWrapper<Task>().eq("status", SystemCode.STATUS_UNPREPROCCESSED.getCode()).or().eq("status", SystemCode.STATUS_PREPROCCESSED_FAILED.getCode()).orderByAsc("create_time"));
        for (Task unpreproccessedTask : unpreproccessedTaskList) {
            resultService.remove(new QueryWrapper<Result>().eq("task_id", unpreproccessedTask.getId()));
            unpreproccessedTask.setStatus(SystemCode.STATUS_PREPROCCESSING.getCode());
            if (taskService.updateById(unpreproccessedTask)) {
                preproccessTaskQueue.offer(unpreproccessedTask);
            }
            log.info("数据预处理队列加入任务：" + unpreproccessedTask.getId());
        }

        List<Task> unclassifiedTaskList = taskService.list(new QueryWrapper<Task>().eq("status", SystemCode.STATUS_PREPROCCESSED_UNCLASSIFIED.getCode()).or().eq("status", SystemCode.STATUS_CLASSIFIED_FAILED.getCode()).orderByAsc("create_time"));
        for (Task unclassifiedTask : unclassifiedTaskList) {
            unclassifiedTask.setStatus(SystemCode.STATUS_CLASSIFYING.getCode());
            if (taskService.updateById(unclassifiedTask)) {
                classifyTaskQueue.offer(unclassifiedTask);
            }
            log.info("流量分类队列加入任务：" + unclassifiedTask.getId());
        }
    }

}