package com.github.simonalong.sample.service;

import com.github.simonalong.autologger.annotation.AutoLogger;
import com.github.simonalong.sample.vo.req.Fun1Req;
import com.github.simonalong.sample.vo.rsp.FunRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author shizi
 * @since 2021-02-07 22:58:25
 */
@Slf4j
@AutoLogger(group = {"fun1", "test"})
@Service
public class BusinessService {

    @AutoLogger(group = "insert")
    public FunRsp autoLogTest(Fun1Req fun1Req) {
        FunRsp rsp = new FunRsp();
        rsp.setAge(fun1Req.getAge());
        rsp.setName("ok");
        return rsp;
    }

    public String debugTest(String name) {
        log.debug("ok,test, {}", name);
        return "getResult";
    }
}
