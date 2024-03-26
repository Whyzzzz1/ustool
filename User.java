package com.imnu.story.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 阿斯亚
 * @date 2023/9/18
 */

/**
 * 用户故事  用户实体类
 */
@Data
@TableName("userstory_users")
public class User {
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户名称
     */
    private String userName;
    /**
     * 性别
     */
    private String sex;
    /**
     * 用户故事
     */
    private String userStory;
}
