package com.company.encryptedtrafficclassifier.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
@TableName("result")
@ApiModel(value = "Result对象", description = "结果信息表")
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("结果ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("任务ID")
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty("置信度")
    @TableField("confidence")
    private String confidence;

    @ApiModelProperty("类别")
    @TableField("category")
    private String category;

    @ApiModelProperty("处理后文件名称")
    @TableField("post_file_name")
    private String postFileName;

    @ApiModelProperty("处理后文件大小（Byte）")
    @TableField("post_file_size")
    private Long postFileSize;

    @ApiModelProperty("处理后文件路径")
    @TableField("post_file_path")
    private String postFilePath;

}