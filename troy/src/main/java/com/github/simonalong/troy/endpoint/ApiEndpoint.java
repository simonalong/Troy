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
    @SuppressWarnings("all")
    @ReadOperation
    public String getList() {
        StringBuffer apiMessage = new StringBuffer();
        apiMessage.append("====================== 分组 ===========================").append("\n");
        apiMessage.append("查询：分组列表          ").append("curl " + apiPrefix() + "/api/troy/log/actuator/group/list").append("\n");
        apiMessage.append("查询：分组函数列表      ").append("curl " + apiPrefix() + "/api/troy/log/actuator/group/fun/list?group={group}").append("\n");
        apiMessage.append("变更：更新分组信息      ").append("curl -X POST " + apiPrefix() + "/api/troy/log/actuator/group?group={group}&logLevel={logLevel}&enable={enable}").append("\n");
        apiMessage.append("变更：某个函数更新      ").append("curl -X POST " + apiPrefix() + "/api/troy/log/actuator/group/fun/change?funId={funId}&logLevel={logLevel}&enable={enable}").append("\n");
        apiMessage.append("-------------------------日志-------------------------------------------------------").append("\n");
        apiMessage.append("查询：分组某个函数信息  ").append("curl " + apiPrefix() + "/api/troy/log/actuator/group/fun/info/one/logger?group={group}&logFunId={funId}").append("\n");
        apiMessage.append("查询：分组函数全部信息  ").append("curl " + apiPrefix() + "/api/troy/log/actuator/group/fun/info/all?group={group}").append("\n");
        apiMessage.append("-------------------------输出--------------------------------------------------------").append("\n");
        apiMessage.append("变更：添加分组输出      ").append("curl -X POST " + apiPrefix() + "/api/troy/log/actuator/group/(console/file/all)?group={group}&logLevel={logLevel}&enable={enable}").append("\n");
        apiMessage.append("变更：添加函数输出      ").append("curl -X POST " + apiPrefix() + "/api/troy/log/actuator/group/fun/print/(console/file/all)?funId={funId}&logLevel={logLevel}&enable={enable}").append("\n");

        apiMessage.append("====================== logger ========================").append("\n");
        apiMessage.append("查询：logger列表        ").append("curl " + apiPrefix() + "/api/troy/log/actuator/logger").append("\n");
        apiMessage.append("查询：logger搜索        ").append("curl " + apiPrefix() + "/api/troy/log/actuator/logger/{loggerName}").append("\n");
        apiMessage.append("更新：logger更新级别    ").append("curl " + apiPrefix() + "/api/troy/log/actuator/logger?loggerName={loggerName}&logLevel={logLevel}").append("\n");
        apiMessage.append("====================== appender ======================").append("\n");
        apiMessage.append("更新：添加输出器        ").append("curl " + apiPrefix() + "/api/troy/log/actuator/appender/console?loggerName={loggerName}&logLevel={logLevel}").append("\n");
        apiMessage.append("删除：删除某个输出器    ").append("curl " + apiPrefix() + "/api/troy/log/actuator/appender?loggerName={loggerName}").append("\n");
        apiMessage.append("删除：删除输出器        ").append("curl " + apiPrefix() + "/api/troy/log/actuator/appender/(console/file/all)?loggerName={loggerName}").append("\n");
        apiMessage.append("======================================================").append("\n");

        return apiMessage.toString();
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

    private String apiPrefix() {
        if (null != localIp && !"".endsWith(localIp) && 0 != port) {
            return "http://" + localIp + ":" + port;
        }
        return "http://localhost:port";
    }
}
