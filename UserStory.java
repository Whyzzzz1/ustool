package com.imnu.story.DO;

//import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 阿斯亚
 * @date 2023/9/14
 */

/**
 * 用户故事实体类
 */
//@TableName("userstory")
@Data
public class UserStory {

    /**
     * 主键
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
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
    /**
     * 用户故事返回结果
     */
    private String storyBack;

}
