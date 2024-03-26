package com.imnu.story.controller;

import com.imnu.story.dto.UserStoriesDTO;
import com.imnu.story.service.PredictionService;
import com.imnu.story.service.UsParseCheckService;
import com.imnu.story.service.UsStrCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 阿斯亚
 * @date 2023/9/14
 */
@Slf4j
@RestController
@RequestMapping("/story")
public class UserStoryController {

    @Resource
    UsStrCheckService usStrCheckService;

    @Resource
    UsParseCheckService usParseCheckService;

    @Resource
    PredictionService predictionService;

    @PostMapping("/struAnalysis")
    public UserStoriesDTO usStrCheck(@RequestBody List<String> texts) {

        List<String> newList = new ArrayList<>();
        for (String item : texts) {
            newList.add(item);
        }
        return usStrCheckService.usStrCheck(newList);
    }

    @PostMapping("/parse")
    public UserStoriesDTO  usCheck(@RequestBody List<String> texts) {

        List<String> newList = new ArrayList<>();
        for (String item : texts) {
            newList.add(item);
        }
        return usParseCheckService.usCheck(newList);
    }
    @PostMapping("/predict")
    public Map<String, Object> predict(@RequestBody List<String> text){
        return predictionService.predict(text);
    }
}
