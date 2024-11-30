package com.company.encryptedtrafficclassifier.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.encryptedtrafficclassifier.entity.JsonResult;
import com.company.encryptedtrafficclassifier.entity.User;
import com.company.encryptedtrafficclassifier.service.UserService;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.utils.CaptchaUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
@Api(tags = "用户信息")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/captcha")
    @ApiOperation(value = "图形验证码接口")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 36);
        captcha.setLen(2);
        CaptchaUtil.out(captcha, request, response);
    }

    @PostMapping(value = "/login")
    @ApiOperation(value = "用户登录接口")
    public JsonResult login(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");
        String captcha = jsonObject.getString("captcha");
        String remember = jsonObject.getString("remember");
        if (StrUtil.hasBlank(userName, password, captcha)) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "用户名、密码或验证码不能为空");
        }
        if (!CaptchaUtil.ver(captcha, request)) {
            CaptchaUtil.clear(request);
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "图形验证码错误");
        }
        User user = userService.getOne(new QueryWrapper<User>().eq("user_name", userName).eq("password", SecureUtil.md5(password)));
        if (null == user) {
            return JsonResult.error(HttpStatus.UNAUTHORIZED.value(), "登录失败，用户名或密码错误");
        }
        StpUtil.login(user.getId(), "1".equals(remember));

        return JsonResult.ok("登录成功");
    }

    @GetMapping("/logout")
    @ApiOperation(value = "用户登出接口")
    public JsonResult logout() {
        if (!StpUtil.isLogin()) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "登出失败，未登录任何账户");
        }
        StpUtil.logout();

        return JsonResult.ok("登出成功");
    }

    @PostMapping("/register")
    @ApiOperation(value = "用户注册接口")
    public JsonResult register(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");
        String confirmPassword = jsonObject.getString("confirmPassword");
        String captcha = jsonObject.getString("captcha");
        if (StrUtil.hasBlank(userName, password, confirmPassword, captcha)) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "用户名、密码或验证码不能为空");
        }
        if (!password.equals(confirmPassword)) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "密码前后两次输入不一致");
        }
        if (!CaptchaUtil.ver(captcha, request)) {
            CaptchaUtil.clear(request);
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "图形验证码错误");
        }
        if (null != userService.getOne(new QueryWrapper<User>().eq("user_name", userName))) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "用户名已存在");
        }
        User user = new User();
        user.setUserName(userName);
        user.setPassword(SecureUtil.md5(password));
        if (!userService.save(user)) {
            return JsonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "注册失败");
        }

        return JsonResult.ok("注册成功");
    }

    @GetMapping("/info")
    @ApiOperation(value = "查询已登录用户信息接口")
    public JsonResult info() {
        if (!StpUtil.isLogin()) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "查询失败，未登录任何账户");
        }
        User user = userService.getOne(new QueryWrapper<User>().eq("id", StpUtil.getLoginIdAsLong()));
        if (null == user) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "查询失败，用户不存在");
        }

        return JsonResult.ok("查询成功", user);
    }

    @PostMapping("/password")
    @ApiOperation(value = "修改密码接口")
    public JsonResult password(@RequestBody JSONObject jsonObject) {
        String originalPassword = jsonObject.getString("originalPassword");
        String password = jsonObject.getString("password");
        String confirmPassword = jsonObject.getString("confirmPassword");
        if (StrUtil.hasBlank(originalPassword, password, confirmPassword)) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "密码不能为空");
        }
        if (password.equals(originalPassword)) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "新密码不能与原密码相同");
        }
        if (!password.equals(confirmPassword)) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "新密码前后两次输入不一致");
        }
        User user = userService.getOne(new QueryWrapper<User>().eq("id", StpUtil.getLoginIdAsLong()));
        if (!SecureUtil.md5(originalPassword).equals(user.getPassword())) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "原密码错误");
        }
        user.setPassword(SecureUtil.md5(password));
        if (!userService.updateById(user)) {
            return JsonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "修改失败");
        }

        return JsonResult.ok("修改成功");
    }

    @PostMapping("/setting")
    @ApiOperation(value = "基本资料接口")
    public JsonResult setting(@RequestBody JSONObject jsonObject) {
        String userName = jsonObject.getString("userName");
        if (StrUtil.hasBlank(userName)) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "用户名不能为空");
        }
        User user = userService.getOne(new QueryWrapper<User>().eq("id", StpUtil.getLoginIdAsLong()));
        if (userName.equals(user.getUserName())) {
            return JsonResult.error(HttpStatus.BAD_REQUEST.value(), "新用户名不能与原用户名相同");
        }
        user.setUserName(userName);
        if (!userService.updateById(user)) {
            return JsonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "修改失败");
        }

        return JsonResult.ok("修改成功");
    }

    @GetMapping(value = "/list")
    @ApiOperation(value = "分页获取用户列表接口")
    public JsonResult list(
            @RequestParam(name = "page", required = false) Long page,
            @RequestParam(name = "limit", required = false) Long limit,
            @RequestParam(name = "userName", required = false) String userName
    ) {
        if (null != page && null != limit) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.like(null != userName && !"".equals(userName), "user_name", userName);
            IPage<User> userPage = userService.page(new Page<>(page, limit), queryWrapper);

            return JsonResult.ok("查询成功", userPage.getRecords(), userPage.getTotal());
        } else {
            return JsonResult.ok("查询成功", userService.list(), userService.count());
        }
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除用户接口")
    public JsonResult delete(@RequestBody JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("idList");
        List<Long> idList = JSONObject.parseArray(jsonArray.toJSONString(), Long.class);
        if (idList.contains(StpUtil.getLoginIdAsLong())) {
            return JsonResult.error("不能删除当前登录用户");
        }
        if (!userService.removeByIds(idList)) {
            return JsonResult.error("删除失败");
        }

        return JsonResult.ok("删除成功");
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "添加用户接口")
    public JsonResult add(@RequestBody User user) {
        user.setPassword(SecureUtil.md5(user.getPassword()));
        if (!userService.save(user)) {
            return JsonResult.error("添加失败");
        }

        return JsonResult.ok("添加成功");
    }

    @PutMapping(value = "/update")
    @ApiOperation(value = "更新用户接口")
    public JsonResult update(@RequestBody User user) {
        if (user.getId() == StpUtil.getLoginIdAsLong()) {
            return JsonResult.error("不能更新当前登录用户");
        }
        if (!userService.updateById(user)) {
            return JsonResult.error("更新失败");
        }

        return JsonResult.ok("更新成功");
    }

    @GetMapping(value = "/count")
    @ApiOperation(value = "统计用户总数接口")
    public JsonResult count() {
        return JsonResult.ok("统计成功", userService.count());
    }

}