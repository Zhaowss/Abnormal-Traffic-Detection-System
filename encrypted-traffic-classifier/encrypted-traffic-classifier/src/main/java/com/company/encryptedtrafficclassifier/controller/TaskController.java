package com.company.encryptedtrafficclassifier.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.encryptedtrafficclassifier.common.enums.SystemCode;
import com.company.encryptedtrafficclassifier.entity.JsonResult;
import com.company.encryptedtrafficclassifier.entity.Task;
import com.company.encryptedtrafficclassifier.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/task")
@Api(tags = "任务信息")
public class TaskController {

    @Autowired
    TaskService taskService;

    @GetMapping(value = "/list")
    @ApiOperation(value = "分页获取任务列表接口")
    public JsonResult list(
            @RequestParam(name = "page", required = false) Long page,
            @RequestParam(name = "limit", required = false) Long limit,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "status", required = false) String status,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @RequestParam(name = "startTime", required = false) LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @RequestParam(name = "endTime", required = false) LocalDateTime endTime
    ) {
        if (null != page && null != limit) {
            QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(null != type && !"".equals(type), "type", type);
            queryWrapper.eq(null != status && !"".equals(status), "status", status);
            queryWrapper.ge(null != startTime, "create_time", startTime);
            queryWrapper.le(null != endTime, "create_time", endTime);
            queryWrapper.orderByDesc("create_time");
            IPage<Task> taskPage = taskService.page(new Page<>(page, limit), queryWrapper);

            return JsonResult.ok("查询成功", taskPage.getRecords(), taskPage.getTotal());
        } else {
            return JsonResult.ok("查询成功", taskService.list(), taskService.count());
        }
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除任务接口")
    public JsonResult delete(@RequestBody JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("idList");
        List<Long> idList = JSONObject.parseArray(jsonArray.toJSONString(), Long.class);
        if (!taskService.removeByIds(idList)) {
            return JsonResult.error("删除失败");
        }

        return JsonResult.ok("删除成功");
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "新建任务接口")
    public JsonResult add(@RequestBody Task task) {
        task.setStatus(SystemCode.STATUS_UNPREPROCCESSED.getCode());
        if (!taskService.save(task)) {
            return JsonResult.error("新建任务失败");
        }

        return JsonResult.ok("新建任务成功");
    }

    @GetMapping(value = "/count")
    @ApiOperation(value = "统计任务总数接口")
    public JsonResult count() {
        return JsonResult.ok("统计成功", taskService.count());
    }

}