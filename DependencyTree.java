package com.imnu.story.DO;

import lombok.Data;

import java.util.List;

/**
 * @author 阿斯亚
 * @date 2023/10/26
 */
@Data
public class DependencyTree {
    /**
     * 词语列表
     */
    private List<Word> words;
    /**
     * 依存关系列表
     */
    private List<DependencyRelation> dependencyRelations;
    /**
     * 根节点
     */
    private Word root;
}
