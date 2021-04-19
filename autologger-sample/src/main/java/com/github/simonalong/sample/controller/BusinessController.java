package com.github.simonalong.sample.controller;

import com.github.simonalong.autologger.annotation.WatchLogger;
import com.github.simonalong.sample.service.BusinessService;
import com.github.simonalong.sample.vo.req.Fun1Req;
import com.github.simonalong.sample.vo.rsp.FunRsp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author shizi
 * @since 2021-02-07 22:55:29
 */
@RequestMapping("api/sample/biz")
@RestController
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @WatchLogger(group = "insert")
    @PostMapping("autoLoggerTest")
    public FunRsp autoLogTest(@RequestBody Fun1Req fun1Req) {
        return businessService.autoLogTest(fun1Req);
    }

    @WatchLogger
    @GetMapping("addAppender/{parameter}")
    public String debugTest(@PathVariable("parameter") String parameter) {
        return businessService.debugTest(parameter);
    }
}
