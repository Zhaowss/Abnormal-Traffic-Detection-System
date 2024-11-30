package com.company.encryptedtrafficclassifier.service.impl;

import com.company.encryptedtrafficclassifier.entity.Task;
import com.company.encryptedtrafficclassifier.mapper.TaskMapper;
import com.company.encryptedtrafficclassifier.service.TaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
}