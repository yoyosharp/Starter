package com.yoyodev.starter.Service.impl;

import com.yoyodev.starter.Repositories.UserRepository;
import com.yoyodev.starter.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


}
