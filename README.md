## WatchLogger
自动化管理在线日志框架

### 背景
在工作中遇到这么两个问题，然后根据对应问题编写了这么一个框架
1. 线上日志为info级别，但是我想看我的那块debug代码打印的日志，不想重启代码怎么办？
2. 线上出现问题不好排查，想查看某个函数的出入参，不想重启代码怎么办？

对于以上两个问题，该框架提供了对应的方案

## 解决问题1：自动打印函数出入参
要动态实现日志打印，这里需要使用一个注解`@WatchLogger`，修饰类和函数
```java
@Slf4j
@WatchLogger(group = {"fun1", "test"})
@Service
public class BusinessService {
    // ...
}
```
修饰函数，则函数会覆盖类的注解
```java
@Slf4j
@Service
public class XxxService {

    // ...
    @WatchLogger(group = "insert")
    public FunRsp autoLogTest(Fun1Req fun1Req) {
        FunRsp rsp = new FunRsp();
        rsp.setAge(fun1Req.getAge());
        rsp.setName("ok");
        return rsp;
    }
}
```


### 查看分组列表
> http://localhost:8080${log.auto-logger.prefix:/actuator}/group/list

提示：
其中 log.auto-logger.prefix 是可以在application.yml中进行配置，如果没有配置，则默认为/actuator

输出：
> ["default","fun1","test","insert"]

### 查看分组下所有函数
>http://yourService:port${log.auto-logger.prefix:/actuator}/group/fun/list?group={xxx}

输出：
```json
[
  "fun: 13bc77397a3f9a769f3891663ded56ad9e95d556bb2dd1fc27b8d9745111dcd4 = com.github.simonalong.sample.service.BusinessService#debugTest(java.lang.String)",
  "fun: uosdfnwesdfasdfiwuejskkjdfoijaiudoifnasdiufoandfkaiusd9fjjfosiud = com.github.simonalong.sample.service.BusinessService#debugTest2(java.lang.String)"
]
```

### 查看分组所有函数详细信息
>http://yourService:port${log.auto-logger.prefix:/actuator}/group/fun/info/all?group={xxx}

输出：
```json
{
  "13bc77397a3f9a769f3891663ded56ad9e95d556bb2dd1fc27b8d9745111dcd4": {
    "logFunName": "com.github.simonalong.sample.service.BusinessService#debugTest(java.lang.String)",
    "logLevel": "INFO",
    "loggerEnable": false
  },
  "uosdfnwesdfasdfiwuejskkjdfoijaiudoifnasdiufoandfkaiusd9fjjfosiud": {
    "logFunName": "com.github.simonalong.sample.service.BusinessService#debugTest(java.lang.String)",
    "logLevel": "DEBUG",
    "loggerEnable": false
  }
}
```

### 查看函数具体信息
>http://yourService:port${log.auto-logger.prefix:/actuator}/group/fun/info/one/logger?group={group}&funId={funId}

输出：
```json
{
  "logFunName": "com.github.simonalong.sample.service.BusinessService#debugTest(java.lang.String)",
  "logLevel": "INFO",
  "loggerEnable": false
}
```
说明：
其中xxxx=yyy，xxx表示函数对应的唯一标识（funId），yyy表示对应的函数

### 更新分组的级别
>http://yourService:port${log.auto-logger.prefix:/actuator}/group?group={group}&logLevel={level}&enable={enable}' -H 'Content-Type: application/json' 

##### 输出：
> 1/0


### 更新函数的级别
>http://yourService:port${log.auto-logger.prefix:/actuator}/group/fun?group={group}&funId={funId}&logLevel={level}&enable={enable}' -H 'Content-Type: application/json'

##### 说明：

- group：分组
- funId：为上面的一长串，比如 436b1181267238e5958a8be0c1701ebe691ebf8c0038ea1b9179bd34de74fddc
- logLevel：日志级别，trace/debug/info/warn/error，大小写均可，数字也可以，
    - 0：trace
    - 1：debug
    - 2：info
    - 3：warn
    - 4：error
- enable：开启和关闭自动日志，true和false
##### 输出：
> 1/0

输出0，表示没有变更，1表示变更成功


### 日志中的打印
打开自动化日志后，业务运行到某个函数，会自动打印
```text
[auto-logger] 结果：{"response":{"age":12,"name":"ok"},"parameters":[{"age":12,"name":"test"}],"fun":"public com.xxx.controller1.FunRsp com.xxx.controller1.TestController.postFun(com.xxx.controller1.Fun1Req)"}
```
```json
{
  "response":{
    "age":12,
    "name":"ok"
  },
  "parameters":[
    {
      "age":12,
      "name":"test"
    }
  ],
  "fun":"public com.xxx.controller1.FunRsp com.xxx.controller1.TestController.postFun(com.xxx.controller1.Fun1Req)"
}
```
目前打印三个字段：
parameters：函数的入参
response：函数的出参
fun：函数全限定名

## 解决问题2：info级别下打印debug日志
直接操作即可
### 查看root级别
>http://yourService:port${log.auto-logger.prefix:/actuator}/logger/root

输出（举例）：
INFO

### 查看logger
>http://yourService:port${log.auto-logger.prefix:/actuator}/logger

输出：
```json
[
  {
    "loggerName": "ROOT",
    "logLevelStr": "INFO",
    "appenderList": [
      {
        "appenderName": "CONSOLE",
        "appenderPattern": "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(98826){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wEx"
      }
    ]
  }
]
```

### 修改root级别
>curl -X POST http://yourService:port${log.auto-logger.prefix:/actuator}/logger?logLevel={logLevel}

结果（成功/失败）：
0/1

### logger搜索
根据logger名字（代码中默认为类的全名，比如com.xxx）的前缀进行搜索
> curl http://yourService:port${log.auto-logger.prefix:/actuator}/logger/search/list?loggerName={loggerName}

结果，示例：
```json
[
  {
    "loggerName": "com",
    "logLevelStr": "INFO",
    "appenderList": []
  },
  {
    "loggerName": "com.github",
    "logLevelStr": "INFO",
    "appenderList": []
  }
]
```

### 添加appender到控制台
>curl -X POST http://yourService:port${log.auto-logger.prefix:/actuator}/appender/console/{logName}/{logLevel}  -H 'Content-Type: application/json'

### 添加appender到文件
>curl -X POST http://yourService:port${log.auto-logger.prefix:/actuator}/appender/file/{logName}/{logLevel}  -H 'Content-Type: application/json'

### 删除appender到控制台
>curl -X DELETE http://yourService:port${log.auto-logger.prefix:/actuator}/appender/{logName}
