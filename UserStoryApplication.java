package com.imnu.story;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 阿斯亚
 * @date 2023/9/14
 */
@Slf4j
//@MapperScan("com.DO.UserMapper")
//@MapperScan("com.DO.UserStoryMapper")
@SpringBootApplication
public class UserStoryApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(UserStoryApplication.class, args);
        log.info("-------------------------SpringBoot Started------------------------------");
    }
}
