package com.github.simonalong.sample.controller;

import com.github.simonalong.troy.annotation.Watcher;
import com.github.simonalong.sample.service.BusinessService;
import com.github.simonalong.sample.vo.req.Fun1Req;
import com.github.simonalong.sample.vo.rsp.FunRsp;
import com.simonalong.mikilin.annotation.AutoCheck;
import com.simonalong.mikilin.annotation.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author shizi
 * @since 2021-02-07 22:55:29
 */
@AutoCheck
@RequestMapping("api/sample/biz")
@RestController
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @Watcher(group = "insert")
    @PostMapping("troyTest")
    public FunRsp troyTest(@RequestBody Fun1Req fun1Req) {
        return businessService.troyTest(fun1Req);
    }

    @Watcher(group = "insert2")
    @PostMapping("troyTest2")
    public FunRsp troyTest2(@RequestBody Fun1Req fun1Req) {
        return businessService.troyTest(fun1Req);
    }

    @GetMapping("addAppender/{parameter}")
    public String debugTest(
        @Matcher(value = {"song", "zhou"}, matchChangeTo = "hahah")
        @PathVariable("parameter") String parameter) {
        return businessService.debugTest(parameter);
    }

    @GetMapping("addAppender2/{parameter}")
    public String debugTest2(@PathVariable("parameter") String parameter) {
        return businessService.debugTest2(parameter);
    }
}
