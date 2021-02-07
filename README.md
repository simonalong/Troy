## AutoLogger
自动化管理在线日志框架

### 背景
在工作中遇到这么两个问题，然后根据对应问题编写了这么一个框架
1. 线上日志为info级别，但是我想看我的那块debug代码打印的日志，不想重启代码怎么办？
2. 线上出现问题不好排查，想查看某个函数的出入参，不想重启代码怎么办？

对于以上两个问题，该框架提供了对应的方案

## 解决问题1：自动打印函数出入参
要动态实现日志打印，这里需要使用一个注解`@AutoLogger`，修饰类和函数
```java
@Slf4j
@AutoLogger(group = {"fun1", "test"})
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
    @AutoLogger(group = "insert")
    public FunRsp autoLogTest(Fun1Req fun1Req) {
        FunRsp rsp = new FunRsp();
        rsp.setAge(fun1Req.getAge());
        rsp.setName("ok");
        return rsp;
    }
}
```

### 查看分组
> curl http://yourService:port/actuator/auto-group

输出：
> ["default","fun1","test","insert"]

### 查看函数
> curl http://yourService:port/actuator/auto-fun/{group}

输出：
```json
[
    "fun: 436b1181267238e5958a8be0c1701ebe691ebf8c0038ea1b9179bd34de74fddc = com.simon.base.service1.TestService#postFun(com.simon.base.controller1.Fun1Req)",
    "fun: 6581075c99b768d2d708432255fcee0d94cea6afa53d290c9317f9ce42475ee8 = com.simon.base.controller1.TestController#postFun(com.simon.base.controller1.Fun1Req)"
]
```
说明：
其中xxxx=yyy，xxx表示函数对应的唯一标识（funId），yyy表示对应的函数
### 查看服务当前日志状态
> curl http://yourService:port/actuator/auto-fun/{group}/{funId}

输出：
```json
{
    "logLevel": "INFO",
    "loggerEnable": false
}
```
### 修改函数自动日志
> curl -X POST  http://yourService:port/actuator/auto-fun/{group}/{funId}/{logLevel}/{enable}

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
### 修改分组下所有函数日志
> curl -X POST http://yourService:port/actuator/auto-group/{group}/{logLevel}/{enable}

##### 输出：
> n

表示修改了多少个函数
### 日志中的打印
打开自动化日志后，业务运行到某个函数，会自动打印
```text
[auto-logger] 结果：{"response":{"age":12,"name":"ok"},"parameters":[{"age":12,"name":"test"}],"fun":"public com.simon.base.controller1.FunRsp com.simon.base.controller1.TestController.postFun(com.simon.base.controller1.Fun1Req)"}
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
  "fun":"public com.simon.base.controller1.FunRsp com.simon.base.controller1.TestController.postFun(com.simon.base.controller1.Fun1Req)"
}
```
目前打印三个字段：
parameters：函数的入参
response：函数的出参
fun：函数全限定名

## 解决问题2：info级别下打印debug日志
直接操作即可
### 修改root级别
> curl -X POST  http://yourService:port/actuator/logger-root-set/{logLevel}

结果（成功/失败）：
0/1

### logger搜索
根据logger名字（代码中默认为类的全名，比如com.xxx）的前缀进行搜索
> curl  http://yourService:port/actuator/logger-search/{loggerNamePrefix}

结果，示例：
```json
[
    {
        "debugEnabled": true,
        "effectiveLevel": {
            "levelInt": 10000,
            "levelStr": "DEBUG"
        },
        "errorEnabled": true,
        "infoEnabled": true,
        "name": "com.github.simonalong.sample.service",
        "traceEnabled": false,
        "warnEnabled": true
    },
    {
        "debugEnabled": true,
        "effectiveLevel": {
            "$ref": "$[0].effectiveLevel"
        },
        "errorEnabled": true,
        "infoEnabled": true,
        "name": "com.github.simonalong.sample.service.BusinessService",
        "traceEnabled": false,
        "warnEnabled": true
    }
]
```

### 添加appender到控制台
该操作就是给日志添加对应的打印的，就是打印到哪里
> curl -X POST  http://yourService:port/actuator/add-appender-console/{loggerName}/{logLevel}  -H 'Content-Type: application/json'

结果：
0/1

添加后，就可以直接进行测试了

### 删除到控制台的appender
对于打开后测试完毕，可以再进行关闭
> curl -X DELETE  http://yourService:port/actuator/add-appender-console/{loggerName}

结果：
0/1

### 添加appender打印到文件
> curl -X POST  http://yourService:port/actuator/add-appender-file/{loggerName}/{logLevel}/{logHome}

结果：
0/1

说明：
其中logHome由于是通过url传入进去，因此"/"不能用，这里用"="代替
> =user=home=test<br/>

表示
> /user/home/test

### 删除文件对应的appender
> curl -X DELETE  http://yourService:port/actuator/add-appender-file/{loggerName}

结果：
0/1
