package com.github.simonalong.sample.service;

import com.github.simonalong.troy.annotation.Watcher;
import com.github.simonalong.sample.vo.req.Fun1Req;
import com.github.simonalong.sample.vo.rsp.FunRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author shizi
 * @since 2021-02-07 22:58:25
 */
@Slf4j
@Watcher(group = {"fun1", "test"})
@Service
public class BusinessService {

    @Watcher(group = "insert")
    public FunRsp troyTest(Fun1Req fun1Req) {
        FunRsp rsp = new FunRsp();
        rsp.setAge(fun1Req.getAge());
        rsp.setName(fun1Req.getName());
        return rsp;
    }

    @Watcher(group = "insert2")
    public FunRsp troyTest2(Fun1Req fun1Req) {
        FunRsp rsp = new FunRsp();
        rsp.setAge(fun1Req.getAge());
        rsp.setName("ok");
        return rsp;
    }

    public String debugTest(String name) {
        log.debug("ok,test, {}", name);
        return name;
    }

    public String debugTest2(String name) {
        log.debug("ok,test2, {}", name);
        return name;
    }
}
