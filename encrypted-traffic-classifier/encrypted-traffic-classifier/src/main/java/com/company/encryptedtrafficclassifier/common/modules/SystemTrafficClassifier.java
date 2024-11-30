package com.company.encryptedtrafficclassifier.common.modules;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.company.encryptedtrafficclassifier.common.constants.SystemConstant;
import com.company.encryptedtrafficclassifier.common.enums.SystemCode;
import com.company.encryptedtrafficclassifier.entity.Result;
import com.company.encryptedtrafficclassifier.entity.Task;
import com.company.encryptedtrafficclassifier.service.ResultService;
import com.company.encryptedtrafficclassifier.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class SystemTrafficClassifier {

    @Autowired
    ResultService resultService;

    @Autowired
    TaskService taskService;

    @Scheduled(fixedDelay = 20 * 1000)
    public void classifyTask() {
        Task task = SystemTaskScheduler.classifyTaskQueue.poll();

        if (task != null) {
            log.info("执行流量分类任务：" + task.getId());

            try {
                String url = SystemConstant.FLASK_SERVER_IP + SystemConstant.FLASK_SERVER_CLASSIFY;
                RequestConfig requestConfig = RequestConfig.custom()
                        .setConnectTimeout(Timeout.ofMilliseconds(10 * 60 * 1000L))
                        .setConnectionRequestTimeout(Timeout.ofMilliseconds(10 * 60 * 1000L))
                        .setResponseTimeout(Timeout.ofMilliseconds(10 * 60 * 1000L))
                        .build();

                List<Result> unclassifiedResultList = resultService.list(new QueryWrapper<Result>().eq("task_id", task.getId()));
                for (Result unclassifiedResult : unclassifiedResultList) {
                    File file = new File(unclassifiedResult.getPostFilePath());
                    String responseContent;
                    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                        HttpPost httpPost = new HttpPost(url);
                        httpPost.setConfig(requestConfig);

                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        builder.setCharset(StandardCharsets.UTF_8);
                        builder.addTextBody("taskType", task.getType(), ContentType.TEXT_PLAIN);
                        builder.addBinaryBody("imageFile", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
                        HttpEntity requestEntity = builder.build();
                        httpPost.setEntity(requestEntity);

                        try (CloseableHttpResponse httpResponse = httpclient.execute(httpPost)) {
                            log.info(httpResponse.getReasonPhrase());
                            log.info(String.valueOf(httpResponse.getVersion()));
                            log.info(String.valueOf(httpResponse.getCode()));

                            HttpEntity responseEntity = httpResponse.getEntity();
                            responseContent = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                            JSONObject jsonObject = JSONObject.parseObject(responseContent);
                            String category = jsonObject.getJSONObject("data").getString("category");
                            String confidence = jsonObject.getJSONObject("data").getString("confidence");

                            unclassifiedResult.setCategory(category);
                            unclassifiedResult.setConfidence(confidence);
                            resultService.updateById(unclassifiedResult);
                        }
                    } catch (IOException | ParseException | NoClassDefFoundError e) {
                        log.error(e.getMessage());

                        task.setStatus(SystemCode.STATUS_CLASSIFIED_FAILED.getCode());
                        taskService.updateById(task);

                        return;
                    }
                }

                task.setStatus(SystemCode.STATUS_CLASSIFIED.getCode());
                taskService.updateById(task);

            } catch (Exception e) {
                log.error(e.getMessage());

                task.setStatus(SystemCode.STATUS_CLASSIFIED_FAILED.getCode());
                taskService.updateById(task);
            }
        }
    }

}