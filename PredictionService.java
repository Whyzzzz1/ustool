package com.imnu.story.service;

import java.util.List;
import java.util.Map;

/**
 * @author 阿斯亚
 * @date 2024/3/20
 */
public interface PredictionService {
    Map<String,Object> predict(List<String> texts);
}
