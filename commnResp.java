package com.imnu.story.VO;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 阿斯亚
 * @date 2023/9/13
 */
@Data
public class commnResp<T>{
    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    public static <T> commnResp<T> success(T object) {
        commnResp<T> r = new commnResp<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> commnResp<T> error(String msg) {
        commnResp r = new commnResp();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public commnResp<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
