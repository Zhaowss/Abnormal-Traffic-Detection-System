package com.company.encryptedtrafficclassifier.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemCode {

    STATUS_UNPREPROCCESSED("0", "未预处理"),
    STATUS_PREPROCCESSING("1", "正在预处理"),
    STATUS_PREPROCCESSED_UNCLASSIFIED("2", "已预处理未分类"),
    STATUS_CLASSIFYING("3", "正在分类"),
    STATUS_CLASSIFIED("4", "已分类"),
    STATUS_PREPROCCESSED_FAILED("5", "预处理失败"),
    STATUS_CLASSIFIED_FAILED("6", "分类失败");

    @EnumValue
    private final String code;
    private final String description;

}