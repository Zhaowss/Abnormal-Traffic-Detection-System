package com.company.encryptedtrafficclassifier.service.impl;

import com.company.encryptedtrafficclassifier.entity.Traffic;
import com.company.encryptedtrafficclassifier.mapper.TrafficMapper;
import com.company.encryptedtrafficclassifier.service.TrafficService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TrafficServiceImpl extends ServiceImpl<TrafficMapper, Traffic> implements TrafficService {
}