package com.imnu.story.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imnu.story.service.PredictionService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author 阿斯亚
 * @date 2024/3/20
 */
@Service
public class PredictionServiceImpl implements PredictionService {
    @Override
    public Map<String, Object> predict(List<String> texts) {
        List<Map<String, Object>> roleConflictList = new ArrayList<>();
        List<Map<String, Object>> modelConflictList1 = new ArrayList<>();
        List<Map<String, Object>> modelConflictList = new ArrayList<>();
        int totalProblems = 0;
        int errorCount = 0;
        int warningCount = 0;
        int perfectCount = 0;


        // 遍历所有用户故事的两两组合
        for (int i = 0; i < texts.size(); i++) {
            for (int j = i + 1; j < texts.size(); j++) {
                String story1 = texts.get(i);
                String story2 = texts.get(j);
                // 检查这两个故事是否属于相同的角色
                if (!areSameRole(story1, story2)) {
                    // 角色冲突
                    Map<String, Object> roleConflictMap = new LinkedHashMap<>();
                    roleConflictMap.put("story1", story1);
                    roleConflictMap.put("story2", story2);
                    roleConflictMap.put("index1", i);
                    roleConflictMap.put("index2", j);
                    roleConflictList.add(roleConflictMap);
                    continue;
                }
                // 进行模型冲突预测并检查结果
                int prediction = predictConflict(story1, story2);
                Map<String, Object> modelConflictMap = new LinkedHashMap<>();
                Map<String, Object> modelConflictMap1 = new LinkedHashMap<>();
                if (prediction == 1) {
                    // 模型检测到冲突
                    warningCount++;
//                    totalProblems= errorCount + warningCount;

                    modelConflictMap.put("story1", story1);
                    modelConflictMap.put("story2", story2);
                    modelConflictMap.put("usline1", i+1);
                    modelConflictMap.put("usline2", j+1);
                    modelConflictMap.put("errors","模型冲突");
                    modelConflictMap.put("violatedGuidelines","Conflict");
                    modelConflictMap.put("suggestions","请检查模型之间是否冲突");
                    modelConflictMap.put("messages","warming");
                    modelConflictMap.put("totalProblems",warningCount);
                    modelConflictMap.put("errorCount",errorCount);
                    modelConflictMap.put("warningCount",warningCount);
                    modelConflictList.add(modelConflictMap);
                }
                if(prediction == 0) {
                    perfectCount++;
                    modelConflictMap1.put("perfectCount",perfectCount);
                    modelConflictList1.add(modelConflictMap1);
                }

            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
//        result.put("roleConflicts", roleConflictList);
        result.put("modelConflicts", modelConflictList);
        result.put("modelConflicts1", modelConflictList1);
        return result;
    }

    private boolean areSameRole(String story1, String story2) {
        // 提取角色信息（假设 "As a " 后面是角色信息）
        String role1 = extractRole(story1);
        String role2 = extractRole(story2);

        // 比较角色
        return Objects.equals(role1, role2);
    }

    private String extractRole(String story) {
        // 提取角色信息（假设 "As a " 后面是角色信息）
        int startIndex = story.indexOf("As a ") + 5;
        int endIndex = story.indexOf(",", startIndex);
        if (startIndex != -1 && endIndex != -1) {
            return story.substring(startIndex, endIndex).trim();
        }
        return "";
    }

    private int predictConflict(String story1, String story2) {
        // 调用模型服务进行冲突预测
        try {
            // 创建模型服务的URL
            URL url = new URL("https://u223105-b00a-1e6b7388.westc.gpuhub.com:8443/predict");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            // 设置HTTP请求头
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            // 允许输出内容
            con.setDoOutput(true);

            // 将两个故事的组合转换为JSON字符串
            String jsonInputString = "{\"sent1\": \"" + story1 + "\", \"sent2\": \"" + story2 + "\"}";
            // 发送JSON数据到模型服务
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 从模型服务获取返回结果
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                // 使用ObjectMapper解析JSON字符串到Map
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> jsonResponse = objectMapper.readValue(response.toString(), Map.class);
                // 获取预测结果
                int prediction = (int) jsonResponse.get("prediction");
                System.out.println(prediction);
                // 返回预测结果
                return prediction;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 返回-1表示模型检测异常
            return -1;
        }
    }
}
