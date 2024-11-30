package com.company.encryptedtrafficclassifier.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@CrossOrigin
@Controller
@Api(tags = "页面视图")
public class IndexController {

    @GetMapping({"/index", "/login", "/"})
    @ApiOperation(value = "系统入口")
    public String index() {
        if (StpUtil.isLogin()) {
            return "index";
        }
        return "login";
    }

    @GetMapping("/register")
    @ApiOperation(value = "新用户注册")
    public String register() {
        return "register";
    }

    @GetMapping("/userPassword")
    @ApiOperation(value = "修改密码")
    public String userPassword() {
        return "users/user_password";
    }

    @GetMapping("/userSetting")
    @ApiOperation(value = "基本信息")
    public String userSetting() {
        return "users/user_setting";
    }

    @GetMapping("/error")
    @ApiOperation(value = "请求错误")
    public String error() {
        return "error";
    }

    @GetMapping("/userList")
    @ApiOperation(value = "用户管理")
    public String userList() {
        return "users/user_list";
    }

    @GetMapping("/userAdd")
    @ApiOperation(value = "添加用户")
    public String userAdd() {
        return "users/user_add";
    }

    @GetMapping("/userEdit")
    @ApiOperation(value = "更新用户")
    public String userEdit() {
        return "users/user_edit";
    }

    @GetMapping("/trafficList")
    @ApiOperation(value = "流量管理")
    public String trafficList() {
        return "traffics/traffic_list";
    }

    @GetMapping("/trafficGather")
    @ApiOperation(value = "流量捕获")
    public String trafficGather() {
        return "traffics/traffic_gather";
    }

    @GetMapping("/taskList")
    @ApiOperation(value = "任务管理")
    public String taskList() {
        return "tasks/task_list";
    }

    @GetMapping("/taskAdd")
    @ApiOperation(value = "新建任务")
    public String taskUpload() {
        return "tasks/task_add";
    }

    @GetMapping("/resultList")
    @ApiOperation(value = "分类结果")
    public String resultList() {
        return "results/result_list";
    }

    @GetMapping("/resultStatistics")
    @ApiOperation(value = "统计信息")
    public String resultStatistics() {
        return "results/result_statistics";
    }

    @GetMapping("/confidenceStatistics")
    @ApiOperation(value = "分类统计")
    public String confidenceStatistics() {
        return "results/confidence_statistics";
    }

}