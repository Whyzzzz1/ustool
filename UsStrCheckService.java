package com.imnu.story.service;

import com.imnu.story.dto.UserStoriesDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 阿斯亚
 * @date 2023/10/23
 */

public interface UsStrCheckService {

    UserStoriesDTO  usStrCheck(List<String> texts);
}
