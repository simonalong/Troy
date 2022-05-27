package com.github.simonalong.troy.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import static com.github.simonalong.troy.TroyConstants.ENVIRONMENT;


/**
 * @author shizi
 * @since 2021-11-19 19:53:07
 */
@Endpoint(id = ENVIRONMENT)
public class EnvEndpoint {

    /**
     * 配置环境变量
     *
     * @param arg0  set
     * @param key   key
     * @param value value
     * @return 添加结果：0-没有添加成功，1-添加成功
     */
    @WriteOperation
    public Integer setProperty(@Selector String arg0, String key, String value) {
        System.setProperty(key, value);
        return 1;
    }

    /**
     * 读取环境变量
     *
     * @param arg0 get
     * @param key  key
     * @return 添加结果：0-没有添加成功，1-添加成功
     */
    @ReadOperation
    public String getProperty(@Selector String arg0, String key) {
        return System.getProperty(key);
    }
}
