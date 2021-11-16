package com.github.simonalong.troy.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.simonalong.troy.TroyConstants.HELP;

/**
 * @author shizi
 * @since 2021-07-06 00:15:16
 */
@Endpoint(id = HELP)
public class HelpEndpoint {

    private String localIp = null;
    private Integer port = 0;

    /**
     * 获取所有的函数列表
     *
     * @return 分组名列表
     */
    @ReadOperation
    public Object getList() {
        Map<String, Object> infoMap = new LinkedHashMap<>();

        Map<String, String> helpMap = new LinkedHashMap<>();
        helpMap.put("help 查询：命令列表", "curl " + apiPrefix() + "/help");
        helpMap.put("help 查询：修改ip和port", "curl " + withJson() + " '" + apiPrefix() + "/help?ip={ip}&port={port}'");
        infoMap.put("help", helpMap);

        Map<String, String> groupMap = new LinkedHashMap<>();
        groupMap.put("查询：分组列表", "curl '" + apiPrefix() + "/group/list'");
        groupMap.put("查询：分组函数列表", "curl '" + apiPrefix() + "/group/fun/list?group={group}'");
        groupMap.put("变更：更新分组信息", "curl " + withJson() + " '" + apiPrefix() + "/group?group={group}&printLogLevel={logLevel}&enable={enable}'");
        groupMap.put("变更：某个函数更新", "curl " + withJson() + " '" + apiPrefix() + "/group/fun/change?funId={funId}&printLogLevel={logLevel}&enable={enable}'");
        infoMap.put("分组", groupMap);

        Map<String, String> groupLogMap = new LinkedHashMap<>();
        groupLogMap.put("日志查询：分组某个函数信息", "curl '" + apiPrefix() + "/group/fun/info/one/logger?logFunId={funId}'");
        groupLogMap.put("日志查询：分组函数全部信息", "curl '" + apiPrefix() + "/group/fun/info/all?group={group}'");
        infoMap.put("分组-日志", groupLogMap);

        Map<String, String> groupPrintMap = new LinkedHashMap<>();
        groupPrintMap.put("输出变更：添加分组输出", "curl " + withJson() + " '" + apiPrefix() + "/group/(console/file/all)?group={group}&printLogLevel={logLevel}&enable={enable}'");
        groupPrintMap.put("输出变更：添加函数输出", "curl " + withJson() + " '" + apiPrefix() + "/group/fun/print/(console/file/all)?funId={funId}&printLogLevel={logLevel}&enable={enable}'");
        infoMap.put("分组-输出", groupPrintMap);

        Map<String, String> loggerMap = new LinkedHashMap<>();
        loggerMap.put("查询：logger列表", "curl '" + apiPrefix() + "/logger'");
        loggerMap.put("查询：logger搜索", "curl '" + apiPrefix() + "/logger/{loggerName}'");
        loggerMap.put("更新：logger更新并输出", "curl " + withJson() + " '" + apiPrefix() + "/logger/appender/(console/file/all)?loggerName={loggerName}&logLevel={logLevel}'");
        loggerMap.put("更新：logger处理恢复", "curl " + withJson() + " '" + apiPrefix() + "/logger/restore/all/info?loggerName={loggerName}&logLevel={logLevel}'");
        infoMap.put("logger", loggerMap);

        Map<String, String> appenderMap = new LinkedHashMap<>();
        appenderMap.put("更新：添加输出器", "curl " + withJson() + " '" + apiPrefix() + "/appender/(console/file/all)?loggerName={loggerName}&logLevel={logLevel}'");
        appenderMap.put("删除：删除某个输出器", "curl -X DELETE '" + apiPrefix() + "/appender?loggerName={loggerName}'");
        appenderMap.put("删除：删除输出器", "curl -X DELETE '" + apiPrefix() + "/appender/(console/file/all)?loggerName={loggerName}'");
        infoMap.put("appender", appenderMap);

        return infoMap;
    }

    /**
     * 更新ip和port记录，用于展示
     *
     * @param ip   本机ip
     * @param port 端口号
     * @return 1
     */
    @WriteOperation
    public String updateLocalIpAndPortShow(String ip, Integer port) {
        StringBuilder result = new StringBuilder();
        this.localIp = ip;
        this.port = port;
        result.append(1).append("\n");
        return result.toString();
    }

    private String apiPrefix() {
        String apiPre = "/api/troy/log";
        if (null != System.getProperty("management.endpoints.web.basePath") && !"".equals(System.getProperty("management.endpoints.web.basePath"))) {
            apiPre = System.getProperty("management.endpoints.web.basePath");
        }

        if (null != apiPre && apiPre.endsWith("/")) {
            apiPre = apiPre.substring(0, apiPre.length() - 1);
        }

        if (null != localIp && !"".endsWith(localIp) && 0 != port) {
            return "http://" + localIp + ":" + port + apiPre;
        }
        return "http://localhost:port" + apiPre;
    }

    private String withJson() {
        return "-X POST -H 'Content-Type: application/json'";
    }


}
