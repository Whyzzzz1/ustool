package com.imnu.story.VO;

import com.imnu.story.DO.DependencyTree;
import com.imnu.story.DO.NamedEntity;
import edu.stanford.nlp.pipeline.CoreNLPProtos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 阿斯亚
 * @date 2023/10/26
 */
public class NLPResult {
    private List<NamedEntity> namedEntities;  // 命名实体识别结果
    private CoreNLPProtos.ParseTree parseTree;  // 句法分析结果
    private DependencyTree dependencyTree;  // 依存句法分析结果

    private List<String> tokens = new ArrayList<>();  // 分词结果
    private List<String> posTags = new ArrayList<>();  // 词性标注结果

    public void addToken(String token, String posTag) {
        tokens.add(token);
        posTags.add(posTag);
    }

    public void setNamedEntities(List<NamedEntity> namedEntities) {
        this.namedEntities = namedEntities;
    }
}
