package com.github.simonalong.troy.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

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
    @SuppressWarnings("all")
    @ReadOperation
    public String getList() {
        StringBuffer apiMessage = new StringBuffer();
        apiMessage.append("============================================================================================================").append("\n");
        apiMessage.append("help 查询：命令列表          ").append("curl " + apiPrefix() + "/help").append("\n");
        apiMessage.append("help 查询：修改ip和port      ").append("curl " + withJson() + " '" + apiPrefix() + "/help?ip={ip}&port={port}'").append("\n");
        apiMessage.append("============================================").append("\n");
        apiMessage.append("分组 查询：分组列表          ").append("curl '" + apiPrefix() + "/group/list'").append("\n");
        apiMessage.append("分组 查询：分组函数列表      ").append("curl '" + apiPrefix() + "/group/fun/list?group={group}'").append("\n");
        apiMessage.append("分组 变更：更新分组信息      ").append("curl " + withJson() + " '" + apiPrefix() + "/group?group={group}&logLevel={logLevel}&enable={enable}'").append("\n");
        apiMessage.append("分组 变更：某个函数更新      ").append("curl " + withJson() + " '" + apiPrefix() + "/group/fun/change?funId={funId}&logLevel={logLevel}&enable={enable}'").append("\n");
        apiMessage.append("------日志------------").append("\n");
        apiMessage.append("分组 日志查询：分组某个函数信息  ").append("curl '" + apiPrefix() + "/group/fun/info/one/logger?logFunId={funId}'").append("\n");
        apiMessage.append("分组 日志查询：分组函数全部信息  ").append("curl '" + apiPrefix() + "/group/fun/info/all?group={group}'").append("\n");
        apiMessage.append("------输出------------").append("\n");
        apiMessage.append("分组 输出变更：添加分组输出      ").append("curl " + withJson() + " '" + apiPrefix() + "/group/(console/file/all)?group={group}&logLevel={logLevel}&enable={enable}'").append("\n");
        apiMessage.append("分组 输出变更：添加函数输出      ").append("curl " + withJson() + " '" + apiPrefix() + "/group/fun/print/(console/file/all)?funId={funId}&logLevel={logLevel}&enable={enable}'").append("\n");
        apiMessage.append("============================================").append("\n");
        apiMessage.append("logger 查询：logger列表        ").append("curl '" + apiPrefix() + "/logger'").append("\n");
        apiMessage.append("logger 查询：logger搜索        ").append("curl '" + apiPrefix() + "/logger/{loggerName}'").append("\n");
        apiMessage.append("logger 更新：logger更新级别    ").append("curl " + withJson() + " '" + apiPrefix() + "/logger?loggerName={loggerName}&logLevel={logLevel}'").append("\n");
        apiMessage.append("============================================").append("\n");
        apiMessage.append("appender 更新：添加输出器        ").append("curl " + withJson() + " '" + apiPrefix() + "/appender/console?loggerName={loggerName}&logLevel={logLevel}'").append("\n");
        apiMessage.append("appender 删除：删除某个输出器    ").append("curl -X DELETE '" + apiPrefix() + "/appender?loggerName={loggerName}'").append("\n");
        apiMessage.append("appender 删除：删除输出器        ").append("curl -X DELETE '" + apiPrefix() + "/appender/(console/file/all)?loggerName={loggerName}'").append("\n");
        apiMessage.append("============================================================================================================").append("\n");

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
    public String updateLocalIpAndPortShow(String ip, Integer port) {
        StringBuilder result = new StringBuilder();
        this.localIp = ip;
        this.port = port;
        result.append(1).append("\n");
        return result.toString();
    }

    private String apiPrefix() {
        String apiPre = "/api/troy/log/actuator";
        if(null != System.getProperty("management.endpoints.web.basePath") && !"".equals(System.getProperty("management.endpoints.web.basePath"))) {
            apiPre = System.getProperty("management.endpoints.web.basePath");
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
