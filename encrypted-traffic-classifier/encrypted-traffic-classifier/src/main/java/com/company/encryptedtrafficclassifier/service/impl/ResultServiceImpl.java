package com.company.encryptedtrafficclassifier.service.impl;

import com.company.encryptedtrafficclassifier.entity.Result;
import com.company.encryptedtrafficclassifier.mapper.ResultMapper;
import com.company.encryptedtrafficclassifier.service.ResultService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ResultServiceImpl extends ServiceImpl<ResultMapper, Result> implements ResultService {
}