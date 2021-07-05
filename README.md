## Troy
日志在线管理框架，叫特洛伊，有点在暗处操控的意味

# 背景
在工作中遇到这么两个问题，然后根据对应问题编写了这么一个框架
1. 线上日志为info级别，但是我想看我的那块debug代码打印的日志，不想重启代码怎么办？
2. 线上出现问题不好排查，想查看某个函数的出入参，不想重启代码怎么办？

对于以上两个问题，该框架提供了对应的方案，其中日志框架底层采用的是logback
# jar包引入
```xml
<dependency>
    <groupId>com.github.simonalong</groupId>
    <artifactId>troy</artifactId>
    <version>1.0.0</version>
</dependency>
```

配置使用
可以不配置，都有默认值
```yaml
troy:
  logger:
    # api前缀。默认为：/api/troy/logger/actuator
    prefix: /api/troy/logger/actuator
    # 是否启用。默认启用
    enable: true
```

对于该框架有如下几种用法
## 一、分组使用
这里对代码有侵入，提供注解`@WatchLogger`，修饰类和函数，函数会覆盖类的使用。

#### 使用场景：
对于一些平常不需要打印日志，但是在定位问题时候，就需要知道某个函数的出入参这种，就可以使用

```java
@WatchLogger(group = "business")
@RequestMapping("api/sample/biz")
@RestController
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @WatchLogger(group = "insert")
    @PostMapping("troyTest")
    public FunRsp troyTest(@RequestBody Fun1Req fun1Req) {
        return businessService.troyTest(fun1Req);
    }
    // ... 省略更多 ...
}
```
提示：修饰函数，则函数会覆盖类的注解

### 所有分组列表
> curl http://localhost:port/api/auto/logger/actuator/group/list

输出：
```json
[
    "business",
    "insert"
]
```

提示：
其中host和port都是自己业务的，以下都一样

### 分组下的所有函数
> curl http://localhost:port/api/auto/logger/actuator/group/fun/list?group={group}

输出：
```json
[
    "fun: d6290cf8bd8a0c8ac01c4531374e77462ff5d5838c4ff3b1689c76bc41e33b4a = com.github.simonalong.sample.controller.BusinessController#troyTest(com.github.simonalong.sample.vo.req.Fun1Req)",
    "fun: 53cb4ea3745bdb87bc3e8dcd915e98ed4e1128965ce0e3b1dd5d48fc7b05fa59 = com.github.simonalong.sample.service.BusinessService#troyTest(com.github.simonalong.sample.vo.req.Fun1Req)"
]
```

提示：
其中前面是funId，后面是对应的函数展示

### 分组全部信息
> curl http://localhost:port/api/auto/logger/actuator/group/fun/info/all?group={group}

输出：
```json
{
    "d6290cf8bd8a0c8ac01c4531374e77462ff5d5838c4ff3b1689c76bc41e33b4a": {
        "logFunName": "com.github.simonalong.sample.controller.BusinessController#troyTest(com.github.simonalong.sample.vo.req.Fun1Req)",
        "loggerName": "com.github.simonalong.sample.controller.BusinessController",
        "logLevel": "INFO",
        "loggerEnable": false
    },
    "53cb4ea3745bdb87bc3e8dcd915e98ed4e1128965ce0e3b1dd5d48fc7b05fa59": {
        "logFunName": "com.github.simonalong.sample.service.BusinessService#troyTest(com.github.simonalong.sample.vo.req.Fun1Req)",
        "loggerName": "com.github.simonalong.sample.service.BusinessService",
        "logLevel": "INFO",
        "loggerEnable": false
    }
}
```

### 分组函数信息
> curl http://localhost:port/api/auto/logger/actuator/group/fun/info/one/logger?group={group}&logFunId={funId}

输出：
```json
{
    "logFunName": "com.isyscore.os.sample.controller.BusinessController#troyTest(com.isyscore.os.sample.vo.req.Fun1Req)",
    "logLevel": "INFO",
    "loggerEnable": false
}
```

### 全组更新
将分组的所有的函数的日志信息更新
> curl -X POST http://localhost:port/api/auto/logger/actuator/group?group={group}&logLevel={logLevel}&enable={enable}

输出（个数）：
n

### 全组更新并输出到控制台
将分组的所有函数日志信息更新，并添加控制台的appender，输出到控制台
> curl -X POST http://localhost:port/api/auto/logger/actuator/group/console?group={group}&logLevel={logLevel}&enable={enable}

输出（个数）：
n

### 全组更新并输出到文件
将分组的所有函数日志信息更新，并添加文件的appender，输出到文件
> curl -X POST http://localhost:port/api/auto/logger/actuator/group/file?group={group}&logLevel={logLevel}&enable={enable}

输出（个数）：
n

### 全组更新并输出
将分组的所有函数日志信息更新，并添加控制台和文件的appender，输出到控制台和文件
> curl -X POST http://localhost:port/api/auto/logger/actuator/group/all?group={group}&logLevel={logLevel}&enable={enable}

输出（个数）：
n

---

### 分组内函数更新
将分组内的某个函数的日志信息更新
> curl -X POST http://localhost:port/api/auto/logger/actuator/group/fun/change?funId={funId}&logLevel={logLevel}&enable={enable}

输出（个数）：
n

### 分组内函数更新并输出到控制台
将分组的所有函数日志信息更新，并添加控制台的appender，输出到控制台
> curl -X POST http://localhost:port/api/auto/logger/actuator/group/fun/print/console?group={group}&logLevel={logLevel}&enable={enable}

输出（个数）：
n

### 分组内函数更新并输出到文件
将分组的所有函数日志信息更新，并添加文件的appender，输出到文件
> curl -X POST http://localhost:port/api/auto/logger/actuator/group/fun/print/file?group={group}&logLevel={logLevel}&enable={enable}

输出（个数）：
n

### 分组内函数更新并输出
将分组的所有函数日志信息更新，并添加控制台和文件的appender，输出到控制台和文件
> curl -X POST http://localhost:port/api/auto/logger/actuator/group/fun/print/all?group={group}&logLevel={logLevel}&enable={enable}

输出（个数）：
n


## logger（日志记录器）

### 查看所有logger
> curl http://localhost:port/api/auto/logger/actuator/logger

输出：
```json
[
    {
        "loggerName": "ROOT",
        "logLevelStr": "INFO",
        "appenderList": [
            {
                "appenderName": "STDOUT",
                "appenderPattern": "%yellow(%d{yyyy-MM-dd HH:mm:ss.SSS}) %black(shizi-2.local) %highlight(%p) --- %cyan([troy-sample]) %yellow([%X{traceId}]) %black(%c) %black(%M) %black([%t@42976]) : %green(%m%n)"
            }
//       ... 更多 ...
        ]
    }
]
```

### 查看root的日志级别
> curl http://localhost:port/api/auto/logger/actuator/logger/root

输出（DEBUG、INFO、WARN...）：
DEBUG

### logger查找
模糊匹配匹配到的logger
> curl http://localhost:port/api/auto/logger/actuator/logger/search/list?loggerName={loggerName}

输出：
```json
[
    {
        "loggerName": "com.isyscore.os.sample.service.BusinessService",
        "logLevelStr": "INFO",
        "appenderList": []
    }
]
```

### 变更root日志级别
> curl -X POST http://localhost:port/api/auto/logger/actuator/logger/actuator/logger?logLevel={logLevel}

输出（个数）：
n

### 变更某个logger日志级别
> curl -X POST http://localhost:port/api/auto/logger/actuator/logger/name?loggerName={loggerName}&logLevel={logLevel}

输出（个数）：
n

### 变更某个logger并输出到控制台
> curl -X POST http://localhost:port/api/auto/logger/actuator/logger/name/console?loggerName={loggerName}&logLevel={logLevel}

输出（个数）：
n

### 变更某个logger并输出到文件
> curl -X POST http://localhost:port/api/auto/logger/actuator/logger/name/file?loggerName={loggerName}&logLevel={logLevel}

输出（个数）：
n

### 变更某个logger并输出
变更并输出到文件和控制台
> curl -X POST http://localhost:port/api/auto/logger/actuator/logger/name/all?loggerName={loggerName}&logLevel={logLevel}

输出（个数）：
n


## 日志输出器（appender）
该appender主要就是用于将日志搜集之后如何处理使用的

### 添加自定义控制台输出器
添加某个logger的appender到控制台
> curl -X POST http://localhost:port/api/auto/logger/actuator/appender/console?loggerName={loggerName}&logLevel={logLevel}

输出（个数）：
n

### 添加自定义文件输出器
添加某个logger的appender到文件
> curl -X POST http://localhost:port/api/auto/logger/actuator/appender/file?loggerName={loggerName}&logLevel={logLevel}

输出（个数）：
n


### 添加自定义控制台和文件输出器
添加某个logger的appender到文件也到控制台
> curl -X POST http://localhost:port/api/auto/logger/actuator/appender/all?loggerName={loggerName}&logLevel={logLevel}

输出（个数）：
n

---

### 删除某个appender
> curl -X DELETE http://localhost:port/api/auto/logger/actuator/appender?loggerName={LoggerName}

输出（个数）：
n

### 删除自定义文件输出器
> curl -X DELETE http://localhost:port/api/auto/logger/actuator/appender/file?loggerName={LoggerName}

输出（个数）：
n

### 删除自定义控制台输出器
> curl -X DELETE http://localhost:port/api/auto/logger/actuator/appender/console?loggerName={LoggerName}

输出（个数）：
n

### 删除自定义输出器
这里会删除文件也会删除控制台
> curl -X DELETE http://localhost:port/api/auto/logger/actuator/appender/all?loggerName={LoggerName}

输出（个数）：
n

