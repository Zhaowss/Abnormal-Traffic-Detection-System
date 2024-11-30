package com.company.encryptedtrafficclassifier.controller;

import com.alibaba.fastjson.JSONObject;
import com.company.encryptedtrafficclassifier.entity.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/file")
@Api(tags = "文件信息")
public class FileController {

    @PostMapping(value = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "上传文件接口")
    public JsonResult upload(@RequestPart(name = "file") MultipartFile file) throws IOException {
        File fileDirectory = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "files");
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        String originalFileName = file.getOriginalFilename();
        String fileSuffix = "";
        String filePrefix = "";
        if (originalFileName != null) {
            fileSuffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            filePrefix = originalFileName.substring(originalFileName.lastIndexOf(File.separator) + 1, originalFileName.lastIndexOf("."));
        }
        String fileName = filePrefix + "_" + System.currentTimeMillis() + fileSuffix;
        String filePath = fileDirectory + File.separator + fileName;
        Long fileSize = file.getSize();
        file.transferTo(new File(filePath));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileName", fileName);
        jsonObject.put("filePath", filePath);
        jsonObject.put("fileSize", fileSize);

        return JsonResult.ok("上传成功", jsonObject);
    }

    @GetMapping(value = "/download/{fileName}")
    @ApiOperation(value = "下载文件接口")
    public ResponseEntity download(
            @PathVariable(name = "fileName") String fileName,
            @RequestParam(name = "parentName", required = false) String parentName
    ) throws FileNotFoundException {
        File fileDirectory = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "files");
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        String filePath;
        if (null == parentName) {
            filePath = fileDirectory + File.separator + fileName;
        } else {
            filePath = fileDirectory + File.separator + parentName + File.separator + fileName;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }

}