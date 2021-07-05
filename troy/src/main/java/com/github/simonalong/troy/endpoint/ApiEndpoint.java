package com.github.simonalong.troy.endpoint;

import com.alibaba.fastjson.JSON;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import java.util.*;

import static com.github.simonalong.troy.TroyConstants.API;

/**
 * @author shizi
 * @since 2021-07-06 00:15:16
 */
@Endpoint(id = API)
public class ApiEndpoint {

    private String localIp = null;
    private Integer port = 0;

    /**
     * 获取所有的函数列表
     *
     * @return 分组名列表
     */
    @ReadOperation
    public String getList() {
        Map<String, Map<String, String>> apiMap = new LinkedHashMap<>();
        apiMap.put("分组", generateGroup());
        apiMap.put("logger", generateLogger());
        apiMap.put("appender", generateAppender());
        return JSON.toJSONString(apiMap);
    }

    /**
     * 更新ip和port记录，用于展示
     *
     * @param ip   本机ip
     * @param port 端口号
     * @return 1
     */
    @WriteOperation
    public Integer updateLocalIpAndPortShow(String ip, Integer port) {
        this.localIp = ip;
        this.port = port;
        return 1;
    }

    private Map<String, String> generateGroup() {
        Map<String, String> pair = new LinkedHashMap<>();
        pair.put("查询：分组列表", "curl " + apiPrefix() + "/api/troy/log/actuator/group/list");
        pair.put("查询：分组函数列表", "curl " + apiPrefix() + "/api/troy/log/actuator/group/fun/list?group={group}");
        pair.put("变更：更新分组信息", "curl -X POST " + apiPrefix() + "/api/troy/log/actuator/group?group={group}&logLevel={logLevel}&enable={enable}");
        pair.put("变更：某个函数更新", "curl -X POST " + apiPrefix() + "/api/troy/log/actuator/group/fun/change?funId={funId}&logLevel={logLevel}&enable={enable}");
        pair.put("-----日志-----", "--------------------------------------------------");
        pair.put("查询：分组某个函数信息", "curl " + apiPrefix() + "/api/troy/log/actuator/group/fun/info/one/logger?group={group}&logFunId={funId}");
        pair.put("查询：分组函数全部信息", "curl " + apiPrefix() + "/api/troy/log/actuator/group/fun/info/all?group={group}");
        pair.put("----输出------", "--------------------------------------------------");
        pair.put("变更：添加分组输出", "curl -X POST " + apiPrefix() + "/api/troy/log/actuator/group/(console/file/all)?group={group}&logLevel={logLevel}&enable={enable}");
        pair.put("变更：添加函数输出", "curl -X POST " + apiPrefix() + "/api/troy/log/actuator/group/fun/print/(console/file/all)?funId={funId}&logLevel={logLevel}&enable={enable}");
        return pair;
    }

    private Map<String, String> generateLogger() {
        Map<String, String> pair = new LinkedHashMap<>();
        pair.put("查询：logger列表", "curl " + apiPrefix() + "/api/troy/log/actuator/logger");
        pair.put("查询：logger搜索", "curl " + apiPrefix() + "/api/troy/log/actuator/logger/{loggerName}");
        pair.put("更新：logger更新级别", "curl " + apiPrefix() + "/api/troy/log/actuator/logger?loggerName={loggerName}&logLevel={logLevel}");
        return pair;
    }

    private Map<String, String> generateAppender() {
        Map<String, String> pair = new LinkedHashMap<>();
        pair.put("更新：添加输出器", "curl " + apiPrefix() + "/api/troy/log/actuator/appender/console?loggerName={loggerName}&logLevel={logLevel}");
        pair.put("删除：删除某个输出器", "curl " + apiPrefix() + "/api/troy/log/actuator/appender?loggerName={loggerName}");
        pair.put("删除：删除输出器", "curl " + apiPrefix() + "/api/troy/log/actuator/appender/(console/file/all)?loggerName={loggerName}");
        return pair;
    }

    private String apiPrefix() {
        if (null != localIp && !"".endsWith(localIp) && 0 != port) {
            return "http://" + localIp + ":" + port;
        }
        return "http://localhost:port";
    }
}
