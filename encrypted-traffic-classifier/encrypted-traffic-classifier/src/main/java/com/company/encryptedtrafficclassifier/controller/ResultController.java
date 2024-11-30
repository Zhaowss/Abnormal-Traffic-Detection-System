package com.company.encryptedtrafficclassifier.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.encryptedtrafficclassifier.common.enums.SystemCode;
import com.company.encryptedtrafficclassifier.entity.JsonResult;
import com.company.encryptedtrafficclassifier.entity.Result;
import com.company.encryptedtrafficclassifier.entity.Task;
import com.company.encryptedtrafficclassifier.service.ResultService;
import com.company.encryptedtrafficclassifier.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/result")
@Api(tags = "结果信息")
public class ResultController {

    @Autowired
    ResultService resultService;

    @Autowired
    TaskService taskService;

    @GetMapping(value = "/list")
    @ApiOperation(value = "分页获取结果列表接口")
    public JsonResult list(
            @RequestParam(name = "page", required = false) Long page,
            @RequestParam(name = "limit", required = false) Long limit,
            @RequestParam(name = "taskId") Long taskId
    ) {
        if (null != page && null != limit) {
            QueryWrapper<Result> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("task_id", taskId);
            IPage<Result> resultPage = resultService.page(new Page<>(page, limit), queryWrapper);

            return JsonResult.ok("查询成功", resultPage.getRecords(), resultPage.getTotal());
        } else {
            return JsonResult.ok("查询成功", resultService.list(), resultService.count());
        }
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除结果接口")
    public JsonResult delete(@RequestBody JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("idList");
        List<Long> idList = JSONObject.parseArray(jsonArray.toJSONString(), Long.class);
        if (!resultService.removeByIds(idList)) {
            return JsonResult.error("删除失败");
        }

        return JsonResult.ok("删除成功");
    }

    @GetMapping(value = "/count")
    @ApiOperation(value = "统计结果总数接口")
    public JsonResult count() {
        List<Task> taskList = taskService.list(new QueryWrapper<Task>().eq("status", SystemCode.STATUS_CLASSIFIED.getCode()));

        return JsonResult.ok("统计成功", taskList.size());
    }

    @GetMapping(value = "/stat/pie/{taskType}")
    @ApiOperation(value = "统计饼图接口")
    public JsonResult statPie(@PathVariable(name = "taskType") String taskType) {
        Map<String, Integer> categoryMap = new HashMap<>();
        if (taskType.equals("0")) {
            categoryMap.put("Chat", 0);
            categoryMap.put("Email", 0);
            categoryMap.put("File", 0);
            categoryMap.put("Streaming", 0);
            categoryMap.put("Voip", 0);
            categoryMap.put("Vpn_Chat", 0);
            categoryMap.put("Vpn_Email", 0);
            categoryMap.put("Vpn_File", 0);
            categoryMap.put("Vpn_P2P", 0);
            categoryMap.put("Vpn_Streaming", 0);
            categoryMap.put("Vpn_Voip", 0);
        } else {
            categoryMap.put("ADload", 0);
            categoryMap.put("Adware", 0);
            categoryMap.put("Artemis", 0);
            categoryMap.put("Bunitu", 0);
            categoryMap.put("Dridex", 0);
            categoryMap.put("OpenCandy", 0);
            categoryMap.put("Razy", 0);
            categoryMap.put("Tinba", 0);
            categoryMap.put("TrickBot", 0);
            categoryMap.put("Virtue", 0);
            categoryMap.put("Wannacry", 0);
            categoryMap.put("Zbot", 0);
        }
        List<Task> taskList = taskService.list(new QueryWrapper<Task>().eq("type", taskType).eq("status", SystemCode.STATUS_CLASSIFIED.getCode()));
        for (Task task : taskList) {
            List<Result> resultList = resultService.list(new QueryWrapper<Result>().eq("task_id", task.getId()));
            Map<String, BigDecimal> sums = new HashMap<>();
            Map<String, Integer> counts = new HashMap<>();
            for (Result result : resultList) {
                JSONObject jsonObject = JSONObject.parseObject(result.getConfidence().replace("'", "\""));
                for (String key : jsonObject.keySet()) {
                    BigDecimal value = jsonObject.getBigDecimal(key);
                    sums.put(key, sums.getOrDefault(key, BigDecimal.ZERO).add(value));
                    counts.put(key, counts.getOrDefault(key, 0) + 1);
                }
            }
            Map<String, BigDecimal> averages = new HashMap<>();
            for (String key : sums.keySet()) {
                BigDecimal average = sums.get(key).divide(BigDecimal.valueOf(counts.get(key)), BigDecimal.ROUND_HALF_UP);
                averages.put(key, average);
            }
            String maxKey = null;
            BigDecimal maxAverage = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> entry : averages.entrySet()) {
                if (maxKey == null || entry.getValue().compareTo(maxAverage) > 0) {
                    maxKey = entry.getKey();
                    maxAverage = entry.getValue();
                }
            }
            if (maxKey != null) {
                if (categoryMap.containsKey(maxKey)) {
                    Integer currentValue = categoryMap.get(maxKey);
                    categoryMap.put(maxKey, currentValue + 1);
                }
            }

        }

        JSONArray jsonArray = new JSONArray();
        for (String key : categoryMap.keySet()) {
            Integer value = categoryMap.get(key);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", value);
            jsonObject.put("name", key);
            jsonArray.add(jsonObject);
        }

        return JsonResult.ok("统计成功", jsonArray);
    }

    @GetMapping(value = "/stat/bar/{resultId}")
    @ApiOperation(value = "统计柱状图接口")
    public JsonResult statBar(@PathVariable(name = "resultId") Long resultId) {
        Result result = resultService.getById(resultId);
        if (null == result || null == result.getConfidence()) {
            return JsonResult.error("统计失败");

        }
        JSONObject jsonObject = JSONObject.parseObject(result.getConfidence().replace("'", "\""));

        return JsonResult.ok("统计成功", jsonObject);
    }

    @GetMapping(value = "/stat/barAvg/{taskId}")
    @ApiOperation(value = "统计平均柱状图接口")
    public JsonResult statBarAvg(@PathVariable(name = "taskId") Long taskId) {
        List<Result> resultList = resultService.list(new QueryWrapper<Result>().eq("task_id", taskId));
        Map<String, BigDecimal> sums = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        for (Result result : resultList) {
            JSONObject jsonObject = JSONObject.parseObject(result.getConfidence().replace("'", "\""));
            for (String key : jsonObject.keySet()) {
                BigDecimal value = jsonObject.getBigDecimal(key);
                sums.put(key, sums.getOrDefault(key, BigDecimal.ZERO).add(value));
                counts.put(key, counts.getOrDefault(key, 0) + 1);
            }
        }
        Map<String, BigDecimal> averages = new HashMap<>();
        for (String key : sums.keySet()) {
            BigDecimal average = sums.get(key).divide(BigDecimal.valueOf(counts.get(key)), BigDecimal.ROUND_HALF_UP);
            averages.put(key, average);
        }
        JSONObject jsonObject = new JSONObject();
        for (String key : averages.keySet()) {
            jsonObject.put(key, averages.get(key));
        }

        return JsonResult.ok("统计成功", jsonObject);
    }

    @GetMapping(value = "/finalResult/{taskId}")
    @ApiOperation(value = "最终分类结果接口")
    public JsonResult finalResult(@PathVariable(name = "taskId") Long taskId) {
        List<Result> resultList = resultService.list(new QueryWrapper<Result>().eq("task_id", taskId));
        Map<String, BigDecimal> sums = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        for (Result result : resultList) {
            JSONObject jsonObject = JSONObject.parseObject(result.getConfidence().replace("'", "\""));
            for (String key : jsonObject.keySet()) {
                BigDecimal value = jsonObject.getBigDecimal(key);
                sums.put(key, sums.getOrDefault(key, BigDecimal.ZERO).add(value));
                counts.put(key, counts.getOrDefault(key, 0) + 1);
            }
        }
        Map<String, BigDecimal> averages = new HashMap<>();
        for (String key : sums.keySet()) {
            BigDecimal average = sums.get(key).divide(BigDecimal.valueOf(counts.get(key)), BigDecimal.ROUND_HALF_UP);
            averages.put(key, average);
        }
        String maxKey = null;
        BigDecimal maxAverage = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> entry : averages.entrySet()) {
            if (maxKey == null || entry.getValue().compareTo(maxAverage) > 0) {
                maxKey = entry.getKey();
                maxAverage = entry.getValue();
            }
        }
        if (maxKey == null) {
            maxKey = "未知";
        }

        return JsonResult.ok("计算成功", maxKey);
    }

}