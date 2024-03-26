package com.imnu.story.DO;

import lombok.Data;

/**
 * @author 阿斯亚
 * @date 2023/10/26
 */
@Data
public class Word {
    /**
     * 词语文本
     */
    private String text;
    /**
     * 词性标签
     */
    private String posTag;
    /**
     * 词语在文本中的位置
     */
    private int index;


}
