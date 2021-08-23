package com.github.simonalong.sample.vo.req;

import com.simonalong.mikilin.annotation.Matcher;
import lombok.Data;

/**
 * @author shizi
 * @since 2021-02-04 18:12:50
 */
@Data
public class Fun1Req {

    @Matcher(value = {"song", "zhou"}, matchChangeTo = "hahah", errMsg = "值不合法")
    private String name;
    private Integer age;
}
