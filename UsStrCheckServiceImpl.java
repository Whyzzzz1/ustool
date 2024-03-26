package com.imnu.story.service.impl;

import com.imnu.story.dto.UserStoriesDTO;
import com.imnu.story.service.UsStrCheckService;
import com.imnu.story.utils.NLPService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 阿斯亚
 */
@Service
public class UsStrCheckServiceImpl extends NLPService implements UsStrCheckService {
    /**
     * 结构分析入口：
     * 检测用户故事格式统一，关键字段完整
     * @return
     */
    @Override
    public UserStoriesDTO usStrCheck(List<String> texts) {
        UserStoriesDTO userStoriesDTO  = struUserStories(texts);
        //List<String> keyErrors = (List<String>) parsedResult.get("errors");
        return userStoriesDTO;
    }
    /**
     * 结构分析：
     * 检测用户故事格式统一，关键字段完整
     */
    public static UserStoriesDTO struUserStories(List<String> userStories) {
        String USER_STORY_PATTERN = "^(As a|As an) .+ I want to .+ (so that|in order to) .+$";
        //String USER_STORY_PATTERN = "^(As a|As an) .+ I want to .+( (so that|in order to) .+)?$";
        Pattern pattern = Pattern.compile(USER_STORY_PATTERN);
        Pattern rolePattern = Pattern.compile("^(As a|As an) [A-Za-z\\- ]+");

        List<String> roles = new ArrayList<>();
        List<String> intentions = new ArrayList<>();
        List<String> benefits = new ArrayList<>();
        //违反准则
        List<String> violatedGuidelines = new ArrayList<>();
        // 问题描述
        List<String> errors = new ArrayList<>();
        //建议
        List<String> suggestions = new ArrayList<>();
        //标识
        List<String> messages = new ArrayList<>();
        //用户故事行数
        List<Integer> usline = new ArrayList<>();
        //用户故事
        List<String> userSt = new ArrayList<>();

        int totalProblems = 0;
        int errorCount = 0;
        int warningCount = 0;
        int perfectCount = 0;

        for (int i = 0; i < userStories.size(); i++) {
            String userStory = userStories.get(i);
            boolean hasError = false;
            boolean hasWarning = false;

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

                } else {
                    violatedGuidelines.add("Template not uniform"); //Uniform
                    usline.add(i + 1);
                    //errors.add( (i + 1) + " does not match the expected format.");
                    errors.add("The story does not match the expected format.");
                    suggestions.add("Format the user story as 'As a [role], I want to [intent] so that/in order to [benefit].' Ensure all three parts are present.");
                    hasError = true;
                    userSt.add(userStory);
                }
            } else {
                // 检查用户故事的每个部分
                boolean hasRole = userStory.startsWith("As a") || userStory.startsWith("As an");
                boolean hasIntent = userStory.contains("I want to");
                boolean hasBenefit = userStory.contains("so that") || userStory.contains("in order to");

                if (!hasRole || !rolePattern.matcher(userStory).find()) {
                    violatedGuidelines.add("Well formed");
                    usline.add(i + 1);
                    errors.add("The role part is not formatted correctly. ");
                    suggestions.add("Start with 'As a' or 'As an' followed by a noun or noun phrase.");
                    messages.add("error");
                    hasError = true;
                    userSt.add(userStory);
                }
                if (!hasIntent) {
                    violatedGuidelines.add("Well formed");
                    usline.add(i + 1);
                    errors.add("The intention part is missing or not formatted correctly. ");
                    suggestions.add("Please ensure that the intention part of your user story is led by an action statement that starts with \"I want to\"..");
                    messages.add("error");
                    hasError = true;
                    userSt.add(userStory);
                }
//                if (!hasBenefit) {
//                    violatedGuidelines.add("Well formed");
//                    usline.add(i + 1);
//                    errors.add("The benefit part is missing or not formatted correctly. " );
//                    suggestions.add("Use 'so that' or 'in order to' followed by the benefit or purpose of the action.");
//                    messages.add("warming");
//                    hasWarning = true;
//                    userSt.add(userStory);
//                }
            }
            if (hasError) {
                errorCount++;
                totalProblems++;
//            } else if (hasWarning) {
//                warningCount++;
//                totalProblems++;
            } else {
                // 仅当没有错误且没有警告时增加perfectCount
                perfectCount++;
            }

        }
        UserStoriesDTO userStoriesDTO = new UserStoriesDTO();
        Map<String, Object> result = new LinkedHashMap<>();
        userStoriesDTO.setRoles(roles);
        userStoriesDTO.setIntentions(intentions);
        userStoriesDTO.setBenefits(benefits);
        userStoriesDTO.setUsLine(usline); //用户故事行数
        userStoriesDTO.setErrors(errors); //问题描述
        userStoriesDTO.setViolatedGuidelines(violatedGuidelines); //违反准则
        userStoriesDTO.setSuggestions(suggestions); //建议
        userStoriesDTO.setMessages(messages); //错误和警告标识，存的值是error和warming
        userStoriesDTO.setTotalProblems(totalProblems); //总问题数量
        userStoriesDTO.setErrorCount(errorCount);  //缺陷数量
        userStoriesDTO.setWarningCount(warningCount); //警告数量
        userStoriesDTO.setPerfectCount(perfectCount); //完美数量
        userStoriesDTO.setUserSt(userSt); //用户故事

        System.out.println("Parsed user stories result: " + result);
        return userStoriesDTO;
    }

}
