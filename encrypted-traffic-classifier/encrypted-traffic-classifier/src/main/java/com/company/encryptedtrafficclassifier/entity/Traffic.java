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
@TableName("traffic")
@ApiModel(value = "Traffic对象", description = "流量信息表")
public class Traffic implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("流量ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("协议过滤")
    @TableField("protocol")
    private String protocol;

    @ApiModelProperty("端口过滤")
    @TableField("port")
    private Long port;

    @ApiModelProperty("源IP地址过滤")
    @TableField("source_ip")
    private String sourceIp;

    @ApiModelProperty("目的IP地址过滤")
    @TableField("destination_ip")
    private String destinationIp;

    @ApiModelProperty("时间长度（s）")
    @TableField("time_length")
    private Long timeLength;

    @ApiModelProperty("文件名称")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty("文件大小（Byte）")
    @TableField("file_size")
    private Long fileSize;

    @ApiModelProperty("文件路径")
    @TableField("file_path")
    private String filePath;

    @ApiModelProperty("流量捕获时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}