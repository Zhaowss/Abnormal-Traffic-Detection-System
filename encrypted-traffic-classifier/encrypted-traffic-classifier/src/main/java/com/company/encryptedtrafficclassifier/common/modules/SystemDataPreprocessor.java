package com.company.encryptedtrafficclassifier.common.modules;

import com.company.encryptedtrafficclassifier.common.enums.SystemCode;
import com.company.encryptedtrafficclassifier.common.utils.SystemFileUtil;
import com.company.encryptedtrafficclassifier.entity.Result;
import com.company.encryptedtrafficclassifier.entity.Task;
import com.company.encryptedtrafficclassifier.service.ResultService;
import com.company.encryptedtrafficclassifier.service.TaskService;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SystemDataPreprocessor {

    @Autowired
    ResultService resultService;

    @Autowired
    TaskService taskService;

    @Scheduled(fixedDelay = 15 * 1000)
    public void preprocessTask() {
        Task task = SystemTaskScheduler.preproccessTaskQueue.poll();

        if (task != null) {
            log.info("执行数据预处理任务：" + task.getId());

            try {
                String fileName = task.getPreFileName();

                File fileDirectory = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "files");
                if (!fileDirectory.exists()) {
                    fileDirectory.mkdirs();
                }
                File resultDirectory = new File(fileDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf(".")));
                if (!resultDirectory.exists()) {
                    resultDirectory.mkdirs();
                }
                File preproccessDirectory = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "scripts" + File.separator + "preproccess");
                if (!preproccessDirectory.exists()) {
                    preproccessDirectory.mkdirs();
                }
                File pcapDirectory = new File(preproccessDirectory + File.separator + "1_Pcap");
                if (!preproccessDirectory.exists()) {
                    preproccessDirectory.mkdirs();
                }
                File sessionDirectory = new File(preproccessDirectory + File.separator + "2_Session");
                if (!sessionDirectory.exists()) {
                    sessionDirectory.mkdirs();
                }
                File processedSessionDirectory = new File(preproccessDirectory + File.separator + "3_ProcessedSession");
                if (!processedSessionDirectory.exists()) {
                    processedSessionDirectory.mkdirs();
                }
                File pngDirectory = new File(preproccessDirectory + File.separator + "4_Png");
                if (!pngDirectory.exists()) {
                    pngDirectory.mkdirs();
                }

                Files.copy(Paths.get(fileDirectory + File.separator + fileName), Paths.get(pcapDirectory + File.separator + fileName), StandardCopyOption.REPLACE_EXISTING);
                log.info("已复制: " + Paths.get(fileDirectory + File.separator + fileName) + " -> " + Paths.get(pcapDirectory + File.separator + fileName));

                try (PowerShell powerShell = PowerShell.openSession()) {
                    Map<String, String> config = new HashMap<>();
                    if (task.getType().equals("0")) {
                        config.put("maxWait", "30000");
                    } else {
                        config.put("maxWait", "300000");
                    }
                    PowerShellResponse response = powerShell.executeCommand("cd " + preproccessDirectory);
                    log.info(response.getCommandOutput());

                    response = powerShell.configuration(config).executeScript(preproccessDirectory + File.separator + "1_Pcap2Session.ps1");
                    log.info(response.getCommandOutput());

                    response = powerShell.configuration(config).executeScript(preproccessDirectory + File.separator + "2_ProcessSession.ps1");
                    log.info(response.getCommandOutput());

                    response = powerShell.configuration(config).executeCommand("python " + preproccessDirectory + File.separator + "3_Traffic_anonymity.py");
                    log.info(response.getCommandOutput());

                    response = powerShell.configuration(config).executeScript(preproccessDirectory + File.separator + "4_ProcessSession.ps1");
                    log.info(response.getCommandOutput());

                    response = powerShell.configuration(config).executeCommand("python " + preproccessDirectory + File.separator + "5_Session2Png.py");
                    log.info(response.getCommandOutput());

                } catch (PowerShellNotAvailableException ex) {
                    log.error(ex.getMessage());

                    task.setStatus(SystemCode.STATUS_PREPROCCESSED_FAILED.getCode());
                    taskService.updateById(task);

                    return;
                }

                SystemFileUtil.copyFilteredFiles(pngDirectory, resultDirectory);
                log.info("已复制: " + pngDirectory.getAbsolutePath() + " -> " + resultDirectory.getAbsolutePath());

                FileUtils.cleanDirectory(pcapDirectory);
                log.info("已清除: " + pcapDirectory.getAbsolutePath());

                FileUtils.deleteDirectory(sessionDirectory);
                log.info("已删除: " + sessionDirectory.getAbsolutePath());

                FileUtils.deleteDirectory(processedSessionDirectory);
                log.info("已删除: " + processedSessionDirectory.getAbsolutePath());

                FileUtils.deleteDirectory(pngDirectory);
                log.info("已删除: " + pngDirectory.getAbsolutePath());

                List<File> resultFileList = (List<File>) FileUtils.listFiles(resultDirectory, null, false);
                for (File resultFile : resultFileList) {
                    Result result = new Result();
                    result.setTaskId(task.getId());
                    result.setPostFileName(resultFile.getName());
                    result.setPostFilePath(resultFile.getAbsolutePath());
                    result.setPostFileSize(resultFile.length());
                    resultService.save(result);
                }

                task.setStatus(SystemCode.STATUS_PREPROCCESSED_UNCLASSIFIED.getCode());
                taskService.updateById(task);

            } catch (Exception e) {
                log.error(e.getMessage());

                task.setStatus(SystemCode.STATUS_PREPROCCESSED_FAILED.getCode());
                taskService.updateById(task);
            }
        }
    }

}