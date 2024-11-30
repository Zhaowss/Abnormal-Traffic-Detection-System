package com.company.encryptedtrafficclassifier.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
@TableName("task")
@ApiModel(value = "Task对象", description = "任务信息表")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("任务ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("流量类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("任务执行状态")
    @TableField("status")
    private String status;

    @ApiModelProperty("任务创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("处理前文件名称")
    @TableField("pre_file_name")
    private String preFileName;

    @ApiModelProperty("处理前文件大小（Byte）")
    @TableField("pre_file_size")
    private Long preFileSize;

    @ApiModelProperty("处理前文件路径")
    @TableField("pre_file_path")
    private String preFilePath;

}