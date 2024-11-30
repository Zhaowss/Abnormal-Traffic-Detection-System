package com.company.encryptedtrafficclassifier.service.impl;

import com.company.encryptedtrafficclassifier.entity.User;
import com.company.encryptedtrafficclassifier.mapper.UserMapper;
import com.company.encryptedtrafficclassifier.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}