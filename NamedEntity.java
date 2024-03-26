package com.imnu.story.DO;

import lombok.Data;

/**
 * @author 阿斯亚
 * @date 2023/10/26
 */
@Data
public class NamedEntity {
    /**
     * 命名实体的文本内容
     */
    private String text;
    /**
     * 命名实体的类型（人名、地名、组织名等）
     */
    private String type;
    /**
     * 命名实体在文本中的起始位置
     */
    private int startIndex;
    /**
     * 命名实体在文本中的结束位置
     */
    private int endIndex;

}
