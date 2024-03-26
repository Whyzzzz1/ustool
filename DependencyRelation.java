package com.imnu.story.DO;

import lombok.Data;

/**
 * @author 阿斯亚
 * @date 2023/10/26
 */
@Data
public class DependencyRelation {
    /**
     * 依存词
     */
    private Word governor;
    /**
     * 被依存的词
     */
    private Word dependency;
    /**
     * 依存关系类型
     */
    private String depType;

}
