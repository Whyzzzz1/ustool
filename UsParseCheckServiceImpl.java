package com.imnu.story.service.impl;

import com.imnu.story.Enum.VagueWord_Enum;
import com.imnu.story.dto.UserStoriesDTO;
import com.imnu.story.service.UsParseCheckService;
import com.imnu.story.utils.NLPService;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 阿斯亚
 */
@Service
public class UsParseCheckServiceImpl extends NLPService implements UsParseCheckService {
    private final StanfordCoreNLP pipeline;


    public UsParseCheckServiceImpl() {
        // 创建Stanford NLP管道
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,depparse");
        pipeline = new StanfordCoreNLP(props);
    }

    /**
     * 句法分析入口：
     * 检测用户故事句子成分完整，原子性，最小化
     * @return
     */
    @Override
    public UserStoriesDTO  usCheck(List<String> texts) {
//      UserStoriesDTO parsedResult = NLPService.extractStoryComponents(texts);

       UserStoriesDTO parsedResult = struUserStories(texts);


        Map<String, Object> sentenceResult = checkSentencesMain(parsedResult);
        checkMinimalism(parsedResult);
        checkForVagueness(parsedResult);
        checkAtomicity(parsedResult);
        List<String> sentenceErrors = (List<String>) sentenceResult.get("feedback");
        List<String> keyErrors = parsedResult.getErrors();


        // Combine both lists of errors
        List<String> allErrors = new ArrayList<>();
        if (sentenceErrors != null) {
            allErrors.addAll(sentenceErrors);
        }
        if (keyErrors != null) {
            allErrors.addAll(keyErrors);
        }

        // Print errors (optional, can be removed if not needed)
        if (!allErrors.isEmpty()) {
            System.out.println("Errors found in user stories:");
            for (String error : allErrors) {
                System.out.println(error);
            }
        }

        // Return the combined list of errors in a Map



        return parsedResult;
    }

    /**
     * 检测用户故事句子成份完整入口
     */
    public Map<String, Object> checkSentencesMain(UserStoriesDTO userStories) {

        System.out.println("Parsed result received in checkSentencesMain: " + userStories);
        // 从 parsedResult 中提取 roles, intentions, benefits 列表
        List<String> roles =  userStories.getRoles();
        List<String> intentions = userStories.getIntentions();
        List<String> benefits =  userStories.getBenefits();
        List<String> feedback = checkSentences(roles, intentions, benefits,userStories);

        Map<String, Object> sentenceResult = new HashMap<>();
        sentenceResult.put("feedback", feedback);
        return sentenceResult;
    }

    /**
     * 检测用户故事句子成份
     */
    private List<String> checkSentences(List<String> roles, List<String> intentions, List<String> benefits,UserStoriesDTO userStoriesDTO) {
        List<String> feedbackList = new ArrayList<>();
        System.out.println("checkSentences部分进来了");
        for (int i = 0; i < roles.size(); i++) {
            userStoriesDTO.getFlag().add(true);
            //初始化flag,使列表里面的所有句子的flag的初始值都为true
        }
        for (int i = 0; i < roles.size(); i++) {
            String role = roles.get(i);
            String intention = intentions.get(i);
            String benefit = benefits.get(i);
            StringBuilder feedbackForSentence = new StringBuilder();

            // 检查是否以 "As a" 或 "As an" 开头
            if (role.startsWith("As a ")) {
                // 如果以 "As a " 开头，移除 "As a " 和后面的空格
                //String roleWithoutPrefix = role.substring("As a ".length());
                List<String> roleFeedback = checkRole(role);

                if (roleFeedback != null && !roleFeedback.isEmpty()) {
                    feedbackForSentence.append(String.join(" ", roleFeedback)).append(" ");
                    userStoriesDTO.getUsLine().add(i+1);
                    userStoriesDTO.getViolatedGuidelines().add("The role part of the current user story is unclear or missing.");//角色部分错误
                    userStoriesDTO.getSuggestions().add("Please use a noun or noun phrase in the role part.");//请检查角色部分
                    userStoriesDTO.getMessages().add("error");
                    userStoriesDTO.getErrors().addAll(roleFeedback);
                    userStoriesDTO.setErrorCount(userStoriesDTO.getErrorCount()+1);
                    userStoriesDTO.getFlag().set(i,false);
                }
            } else if (role.startsWith("As an ")) {
                // 如果以 "As an " 开头，移除 "As an " 和后面的空格
                //String roleWithoutPrefix = role.substring("As an ".length());
                List<String> roleFeedback = checkRole(role);

                if (roleFeedback != null && !roleFeedback.isEmpty()) {
                    feedbackForSentence.append(String.join(" ", roleFeedback)).append(" ");
                    userStoriesDTO.getUsLine().add(i+1);
                    userStoriesDTO.getViolatedGuidelines().add("The role part of the current user story is unclear or missing.");
                    userStoriesDTO.getSuggestions().add("Please use a noun or noun phrase in the role part.");
                    userStoriesDTO.getMessages().add("error");
                    userStoriesDTO.getErrors().addAll(roleFeedback);
                    userStoriesDTO.setErrorCount(userStoriesDTO.getErrorCount()+1);
                    userStoriesDTO.getFlag().set(i,false);
                }
            } else {
                // 如果不是以 "As a" 或 "As an" 开头
                feedbackForSentence.append("Role prefix 'As a' or 'As an' is missing or incorrect. ");
                userStoriesDTO.getUsLine().add(i+1);
                userStoriesDTO.getViolatedGuidelines().add("完整性错误");
                userStoriesDTO.getSuggestions().add("请检查xxx");
                userStoriesDTO.getMessages().add("error");
                userStoriesDTO.getErrors().add("Role prefix 'As a' or 'As an' is missing or incorrect. ");
                userStoriesDTO.setErrorCount(userStoriesDTO.getErrorCount()+1);
                userStoriesDTO.getFlag().set(i,false);
            }

            //调用意图部分检测
            String featureFeedback = checkFeature(intention);

            if (featureFeedback != null && !featureFeedback.isEmpty()) {
                feedbackForSentence.append(featureFeedback).append(" ");
                userStoriesDTO.getUsLine().add(i+1);
                userStoriesDTO.getViolatedGuidelines().add("Full sentence");
                userStoriesDTO.getSuggestions().add("Please review the Intent section, as it lacks essential verb or object components.");
                userStoriesDTO.getMessages().add("error");
                userStoriesDTO.getErrors().addAll(Collections.singleton(featureFeedback));//***这里用了collection原来返回是一个String***
                userStoriesDTO.setErrorCount(userStoriesDTO.getErrorCount()+1);
                userStoriesDTO.getFlag().set(i,false);
            }
            //调用目的部分检测
            String benefitFeedback = checkBenefit(benefit);

            if (benefitFeedback != null && !benefitFeedback.isEmpty()) {
                feedbackForSentence.append(benefitFeedback).append(" ");
                userStoriesDTO.getUsLine().add(i+1);
                userStoriesDTO.getViolatedGuidelines().add("Full sentence");
                userStoriesDTO.getSuggestions().add("Please review the Benefit section, as it lacks essential verb or object components.");
                userStoriesDTO.getMessages().add("error");
                userStoriesDTO.getErrors().addAll(Collections.singleton(benefitFeedback));//***这里用了collection原来返回是一个String***
                userStoriesDTO.getFlag().set(i,false);
            }
        }

        return feedbackList;
    }
    /**
     * 检测用户故事句子成份-角色部分
     */
    @NotNull
    private List<String> checkRole(String roleDescription) {
        List<String> errors = new ArrayList<>();
        UserStoriesDTO userStoriesDTO = new UserStoriesDTO();

        if (roleDescription == null || roleDescription.trim().isEmpty()) {
            errors.add("Role description is empty.");
            return errors;
        }

        System.out.println("role部分测试输入内容为：" + roleDescription);
        List<String> nouns = Arrays.asList("NN", "NNS", "NNP", "NNPS");

        // 检查角色描述中是否包含名词
        CoreDocument document = new CoreDocument(roleDescription);
        // 这里假设pipeline已经被初始化
        pipeline.annotate(document);
        boolean nounFound = false;

        for (CoreLabel token : document.tokens()) {
            if (nouns.contains(token.tag())) {
                nounFound = true;
                break;
            }
        }
    // 需要描述
        if (!nounFound) {
            errors.add("Role description '" + roleDescription + "' is missing a noun.");

        }

        return errors;
    }
    /**
     * 检测用户故事句子成份-意图部分
     */
    public String checkFeature(String originalSentence) {
        StringBuilder feedback = new StringBuilder();

        CoreDocument document = new CoreDocument(originalSentence);
        pipeline.annotate(document);

        List<CoreLabel> tokens = document.tokens();

        // Skip "I want to" and any adverbs immediately following it
        int verbIndex = 3; // Index of the first word after "I want to"
        while (verbIndex < tokens.size() && tokens.get(verbIndex).tag().startsWith("RB")) {
            verbIndex++;
        }

        // Check if a verb is found 需要描述
        if (verbIndex >= tokens.size() || !tokens.get(verbIndex).tag().startsWith("VB")) {
            feedback.append("Intention is missing a verb after 'I want to'. ");
        } else {
            // If a verb is found, check for an object or prepositional object
            boolean hasObject = false;
            SemanticGraph graph = document.sentences().get(0).dependencyParse();
            // 打印出依存关系图，以便于调试
            System.out.println(graph.toString(SemanticGraph.OutputFormat.LIST));

            List<SemanticGraphEdge> outgoingEdges = graph.outgoingEdgeList(graph.getNodeByIndex(tokens.get(verbIndex).index()));
            for (SemanticGraphEdge edge : outgoingEdges) {
                String relationType = edge.getRelation().getShortName();
                // 检查直接宾语和介词宾语
                if (Arrays.asList("obj", "dobj", "iobj", "pobj").contains(relationType)) {
                    hasObject = true;
                    break;
                }
                // 检查带有特定介词的名词修饰语，例如 'nmod:for'
                if (relationType.startsWith("nmod") && edge.getRelation().getSpecific() != null && edge.getRelation().getSpecific().equals("for")) {
                    hasObject = true;
                    break;
                }
                // 新增：检查间接宾语，这可能是介词短语宾语
                if (relationType.startsWith("obl")) {
                    hasObject = true;
                    break;
                }
            }

            // Check for compound objects
            if (!hasObject) {
                for (SemanticGraphEdge edge : outgoingEdges) {
                    if (edge.getRelation().getShortName().equals("conj")) {
                        List<SemanticGraphEdge> conjOutgoingEdges = graph.outgoingEdgeList(edge.getTarget());
                        for (SemanticGraphEdge conjEdge : conjOutgoingEdges) {
                            String conjRelationType = conjEdge.getRelation().getShortName();
                            if (Arrays.asList("obj", "dobj", "iobj", "pobj").contains(conjRelationType)) {
                                hasObject = true;
                                break;
                            }
                        }
                    }
                    if (hasObject) {
                        break;
                    }
                }
            }
            // Check for clauses as objects
            if (!hasObject) {
                for (SemanticGraphEdge edge : outgoingEdges) {
                    if (edge.getRelation().getShortName().equals("ccomp") || edge.getRelation().getShortName().equals("xcomp")) {
                        hasObject = true;
                        break;
                    }
                }
            }
            //需要描述
            if (!hasObject) {
                feedback.append("Intention is missing an object after the verb. ");
            }
        }

        if (feedback.length() == 0) {
            return null;
        }

        return feedback.toString().trim();
    }
    /**
     * 检测用户故事句子成份-目的部分
     */
    private String checkBenefit(String sentence) {
        StringBuilder feedback = new StringBuilder();

        // 创建文档对象
        CoreDocument document = new CoreDocument(sentence);
        // 对文档进行注释处理
        pipeline.annotate(document);
        // 获取依存关系图
        SemanticGraph graph = document.sentences().get(0).dependencyParse();

        // 标记是否找到主语和宾语
        boolean hasSubject = false;
        boolean hasVerb = false;
        boolean hasObject = false;

        // 遍历所有的单词节点
        for (IndexedWord word : graph.vertexSet()) {
            // 检查是否有动词
            if (word.tag().startsWith("VB")) {
                hasVerb = true;
            }

            // 获取进入当前单词的所有边（依存关系）
            List<SemanticGraphEdge> incomingEdges = graph.incomingEdgeList(word);
            for (SemanticGraphEdge edge : incomingEdges) {
                // 获取依存关系的类型
                String relationType = edge.getRelation().getShortName();
                // 检查是否有主语
                if (Arrays.asList("nsubj", "nsubjpass").contains(relationType)) {
                    hasSubject = true;
                }
                // 检查是否有宾语
                if (Arrays.asList("obj", "dobj", "iobj", "pobj").contains(relationType)) {
                    hasObject = true;
                }
            }
        }

        // 根据分析结果构建反馈信息 需要
        if (!hasSubject) {

            feedback.append("The benefit part is missing a subject. ");
        }
        if (!hasVerb) {
            feedback.append("The benefit part is missing a verb. ");
        }
        if (!hasObject) {
            feedback.append("The benefit part is missing an object. ");
        }

        // 如果没有发现问题，则返回 null
        if (feedback.length() == 0) {
            return null;
        }

        // 返回反馈信息
        return feedback.toString().trim();
    }



    /**
     * 检测用户故事最小化
     */

    public static UserStoriesDTO  checkMinimalism(UserStoriesDTO parsedResult) {
        // 定义一个正则表达式，用于匹配括号内的内容
        String regex = "\\(.*?\\)|\\[.*?\\]|\\{.*?\\}|‹.*?›|—.*?—|//.*?//";
        // 定义一个正则表达式，用于匹配选择性信息的关键词
        String keywords = "\\bsuch\\b|\\beither\\b|\\binstance\\b|\\bparticularly\\b|\\bNamely\\b|\\bLike\\b|\\bexample\\b|\\bincluding\\b";

        // 创建Pattern对象
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Pattern keyPattern = Pattern.compile(keywords, Pattern.CASE_INSENSITIVE);

        // 检查意图和收益字段
        List<String> intentionErrors = checkField("intentions", parsedResult, pattern, keyPattern);
        List<String> benefitErrors = checkField("benefits", parsedResult, pattern, keyPattern);

        // 将错误信息添加到 parsedResult 的 "feedback" 键中
        parsedResult.getErrors().addAll(intentionErrors);
        parsedResult.getErrors().addAll(benefitErrors);
        return parsedResult;
    }

    private static List<String> checkField(String fieldName, UserStoriesDTO parsedResult, Pattern pattern, Pattern keyPattern) {
        List<String> errors = new ArrayList<>();
        List<String> fieldList = new ArrayList<>();

        if(fieldName.equals("benefits")){
            fieldList = parsedResult.getBenefits();
        }else if(fieldName.equals("intentions")){
            fieldList = parsedResult.getIntentions();
        }
        for (int i = 0; i < fieldList.size(); i++) {
                String fieldContent = fieldList.get(i);
//              Matcher matcher = pattern.matcher(fieldContent);
                Matcher keyMatcher = keyPattern.matcher(fieldContent);
                while (keyMatcher.find()) {
                    String bracketContent = keyMatcher.group();
                    // 行号是索引+1，因为索引是从0开始的
                    String error = "The " + fieldName +" part" + " contains non-minimalist elements: " + bracketContent;
                    errors.add(error);
                    parsedResult.getUsLine().add(i + 1);
                    parsedResult.getViolatedGuidelines().add("Minimal");
                    parsedResult.getSuggestions().add("Please consider removing unnecessary symbols or explanations to enhance clarity and conciseness.");
                    parsedResult.getMessages().add("error");
                    parsedResult.getErrors().addAll(errors);
                    parsedResult.setErrorCount(parsedResult.getErrorCount() + 1);
                    parsedResult.getFlag().set(i,false);

                    errors = new ArrayList<>();
                    break;
                }
                Matcher matcher = pattern.matcher(fieldContent);
                while (matcher.find()) {
                    String error =  "The " + fieldName +" part" + " contains non-minimalist elements: " + fieldContent;
                    errors.add(error);
                    parsedResult.getUsLine().add(i + 1);
                    parsedResult.getViolatedGuidelines().add("Minimal");
                    parsedResult.getSuggestions().add("Please consider removing unnecessary symbols or explanations to enhance clarity and conciseness.");
                    parsedResult.getMessages().add("error");
                    parsedResult.getErrors().addAll(errors);
                    parsedResult.setErrorCount(parsedResult.getErrorCount() + 1);
                    parsedResult.getFlag().set(i,false);

                    errors = new ArrayList<>();
                    break;
                }
        }
        return errors;
    }

    public static UserStoriesDTO  checkForVagueness(UserStoriesDTO parsedResult) {
        //提取模糊词字典
        Set<String> vagueWords = VagueWord_Enum.getWords();


        for (int i = 0; i < parsedResult.getUserSt().size(); i++) {
            String sentence = parsedResult.getUserSt().get(i);

            // 使用正则表达式分词并进行更彻底的文本清洗
            String[] words = sentence.split("\\W+");
            List<String> foundVagueWords = new ArrayList<>();
            for (String word : words) {
                String cleanedWord = word.toLowerCase();
                if (vagueWords.contains(cleanedWord)) {
                    foundVagueWords.add(word);
                }
            }
            if (!foundVagueWords.isEmpty()) {
                parsedResult.getUsLine().add(i + 1);
                parsedResult.getViolatedGuidelines().add("Unambiguous");
                parsedResult.getSuggestions().add("The term may be subject to vague interpretation, a more specific description is recommended.");
                parsedResult.getMessages().add("warning");
                parsedResult.getErrors().add("Possible ambiguous words have been detected in this user story：" + foundVagueWords);
                parsedResult.setWarningCount(parsedResult.getWarningCount() + 1);
                parsedResult.getFlag().set(i, false);
            }
        }
        return parsedResult;
    }


    /**
     * 检测用户故事原子性
     */
    public static UserStoriesDTO checkAtomicity(UserStoriesDTO parsedResult) {
        // 从解析结果中获取意图字段
        List<String> fieldList = parsedResult.getIntentions();
        //完美数量
        int perfectCount = 0;
        // 用于存储违反原子性的意图
        List<String> intentionErrors = new ArrayList<>();
        // 遍历每个意图进行原子性检查
        for (int i = 0; i < fieldList.size(); i++) {
            String intention = fieldList.get(i);            // 如果意图不满足原子性要求
                if (!isSentenceAtomic(intention)) {
                    // 将错误信息添加到列表中
                    intentionErrors.add("This user story violates atomicity");
                    parsedResult.getUsLine().add(i+1);
                    parsedResult.getViolatedGuidelines().add("Atomic");
                    parsedResult.getMessages().add("error");
                    parsedResult.getErrors().addAll(intentionErrors);
                    parsedResult.setErrorCount(parsedResult.getErrorCount()+1);

                    //suggestion
                    parsedResult.getSuggestions().add("Each user story should express a single action. Consider dividing into multiple sub-user stories for clarity and focus");

                    parsedResult.getFlag().set(i,false);
                    //清空intentionErrors，为下一个违反原子性的意图准备
                    intentionErrors = new ArrayList<>();
                }else {
                    if(parsedResult.getFlag().get(i)){
                        perfectCount++;
                        parsedResult.setPerfectCount(perfectCount);
                    }
                }
        }
        parsedResult.setTotalProblems(parsedResult.getErrorCount()+ parsedResult.getWarningCount());
        return parsedResult;
    }

    // 方法：判断一个句子是否符合原子性
    private static boolean isSentenceAtomic(String sentence) {
        // 创建一个文档对象，用于语言处理
        CoreDocument document = new CoreDocument(sentence);
        // 初始化 Stanford CoreNLP pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP();
        // 对文档进行注释，执行依存关系分析
        pipeline.annotate(document);

        // 遍历文档中的每个句子
        for (CoreSentence coreSentence : document.sentences()) {
            // 获取句子的依存关系图
            SemanticGraph dependencyGraph = coreSentence.dependencyParse();
            // 计算句子中根动词的数量
            long rootVerbsCount = dependencyGraph.vertexListSorted().stream()
                    .filter(node -> node.tag().startsWith("VB") && dependencyGraph.getRoots().contains(node))
                    .count();

            // 检查是否所有的连词连接的成分都是独立的
            boolean hasIndependentConjuncts = dependencyGraph.edgeListSorted().stream()
                    .filter(edge -> edge.getRelation().toString().equals("cc"))
                    .allMatch(edge -> areConjunctsIndependent(edge, dependencyGraph));

            // 如果存在多于一个根动词或连词连接的成分不是独立的，则不符合原子性
            if (rootVerbsCount > 1 || !hasIndependentConjuncts) {
                return false;
            }
        }
        // 如果所有条件都满足，则句子符合原子性
        return true;
    }

    // 方法：判断通过连词连接的两个成分是否独立
    private static boolean areConjunctsIndependent(SemanticGraphEdge edge, SemanticGraph graph) {
        // 获取连词连接的两个成分
        IndexedWord conjunct1 = edge.getGovernor();
        IndexedWord conjunct2 = edge.getDependent();

        // 在依存关系图中查找所有的 'conj' 关系
        List<SemanticGraphEdge> conjEdges = graph.findAllRelns(UniversalEnglishGrammaticalRelations.CONJUNCT);

        // 分别检查两个成分是否与独立的动词相关联
        boolean conjunct1Independent = isConjunctIndependent(conjunct1, conjEdges);
        boolean conjunct2Independent = isConjunctIndependent(conjunct2, conjEdges);

        // 如果两个成分都独立，则返回 true
        return conjunct1Independent && conjunct2Independent;
    }

    // 方法：判断一个成分是否与独立的动词相关联
    private static boolean isConjunctIndependent(IndexedWord conjunct, List<SemanticGraphEdge> conjEdges) {
        // 遍历所有的 'conj' 关系
        for (SemanticGraphEdge conjEdge : conjEdges) {
            // 检查当前关系是否包含指定的成分
            if (conjEdge.getGovernor().equals(conjunct) || conjEdge.getDependent().equals(conjunct)) {
                // 检查连词连接的成分是否有独立的动词或动词短语
                if (hasIndependentVerb(conjEdge.getGovernor(), conjEdge.getDependent())) {
                    return true;
                }
            }
        }
        // 如果没有与独立的动词相关联的成分，则返回 false
        return false;
    }

    // 方法：检查两个成分是否分别包含或与动词相关
    private static boolean hasIndependentVerb(IndexedWord word1, IndexedWord word2) {
        // 检查两个成分是否分别是动词或与动词相关
        return word1.tag().startsWith("VB") || word2.tag().startsWith("VB");
    }

}
