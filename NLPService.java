package com.imnu.story.utils;

import com.imnu.story.dto.UserStoriesDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author 阿斯亚
 */
@Service
public class NLPService {


    /**
     * 成分提取
     * 检测用户故事格式统一，关键字段完整
     * @return
     */
    public static UserStoriesDTO extractStoryComponents(List<String> userStories) {
        final String USER_STORY_PATTERN = "^(As a|As an) .+ I want to .+ (so that|in order to) .+$";
        Pattern pattern = Pattern.compile(USER_STORY_PATTERN);

        UserStoriesDTO dto = new UserStoriesDTO();

        for (String userStory : userStories) {
            Matcher matcher = pattern.matcher(userStory);
            if (matcher.find()) {
                // 分割用户故事以获取角色、意图和原因
                String[] parts = userStory.split(" I want to | so that | in order to ");
                if (parts.length >= 3) {
                    // 添加角色
                    dto.getRoles().add(parts[0].trim());
                    // 添加意图
                    dto.getIntentions().add("I want to " + parts[1].trim());
                    // 添加原因
                    dto.getBenefits().add(parts[2].trim());
                }
            }
        }
        return dto;
    }

    public static UserStoriesDTO struUserStories(List<String> userStories) {
        String USER_STORY_PATTERN = "^(As a|As an) .+ I want to .+ (so that|in order to) .+$";
        //String USER_STORY_PATTERN = "^(As a|As an) .+ I want to .+( (so that|in order to) .+)?$";
        Pattern pattern = Pattern.compile(USER_STORY_PATTERN);
        Pattern rolePattern = Pattern.compile("^(As a|As an) [A-Za-z\\- ]+");

        List<String> roles = new ArrayList<>();
        List<String> intentions = new ArrayList<>();
        List<String> benefits = new ArrayList<>();
        //用户故事行数
        List<Integer> usLine = new ArrayList<>();
        //用户故事
        List<String> userSt = new ArrayList<>();

        for (int i = 0; i < userStories.size(); i++) {
            String userStory = userStories.get(i);
            System.out.println("Processing user story: " + userStory);

            Matcher matcher = pattern.matcher(userStory);
            if (matcher.find()) {
                // 分割用户故事以获取角色、意图和原因
                String[] parts = userStory.split(" I want to | so that | in order to ");
                if (parts.length >= 3) {
                    // 添加角色
                    roles.add(parts[0].trim());
                    // 添加意图
                    intentions.add("I want to " + parts[1].trim());
                    // 添加原因
                    benefits.add(parts[2].trim());
                    // 请记录完美的数量，在此补充代码
                  //  usLine.add(i + 1); // 记录用户故事的行号
                    userSt.add(userStory); // 记录用户故事本身

                }
            }
        }
        UserStoriesDTO userStoriesDTO = new UserStoriesDTO();
        userStoriesDTO.setRoles(roles);
        userStoriesDTO.setIntentions(intentions);
        userStoriesDTO.setBenefits(benefits);
        //userStoriesDTO.setUsline(usLine); //用户故事行数
        userStoriesDTO.setUserSt(userSt); //用户故事

        return userStoriesDTO;
    }

}



