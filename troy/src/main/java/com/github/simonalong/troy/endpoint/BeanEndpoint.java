package com.github.simonalong.troy.endpoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.simonalong.troy.util.ObjectUtil;
import com.github.simonalong.troy.util.Pair;
import com.github.simonalong.troy.util.SpringBeanUtils;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import static com.github.simonalong.troy.TroyConstants.BEAN;

/**
 * @author shizi
 * @since 2021-11-19 19:53:07
 */
@RestControllerEndpoint(id = BEAN)
public class BeanEndpoint {

    /**
     * 查询对应的bean名字
     *
     * @param name bean的名字
     * @return bean的所有名字
     */
    @GetMapping("names")
    public Object getAllBean(String name) {
        List<String> beanNameList = new ArrayList<>();
        String[] beanNames = SpringBeanUtils.getAllBean();
        if (null == beanNames) {
            return null;
        }

        if (StringUtils.isEmpty(name)) {
            return JSON.toJSONString(beanNames);
        }

        for (String beanName : beanNames) {
            if (beanName.toLowerCase().contains(name.toLowerCase())) {
                beanNameList.add(beanName);
            }
        }
        return JSON.toJSONString(beanNameList);
    }

    /**
     * 查询bean对应属性的值
     *
     * @param name  bean的名字
     * @param field bean对应的属性
     * @return bean对应属性的值
     */
    @GetMapping("get/field")
    public Object getBeanFieldValue(String name, String field) {
        Object bean = SpringBeanUtils.getBean(name);
        if (null == bean) {
            return null;
        }

        try {
            Field field1 = bean.getClass().getDeclaredField(field);
            field1.setAccessible(true);
            return field1.get(bean);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 查询bean对应属性的值
     *
     * @param name  bean的名字
     * @return bean对应属性的值
     */
    @PostMapping("set/field")
    public Object setBeanFieldValue(String name, @RequestBody JSONObject fieldValue) {
        Object bean = SpringBeanUtils.getBean(name);
        if (null == bean) {
            return null;
        }

        List<Pair<String, Object>> fieldPair = new ArrayList<>();
        fieldValue.forEach((k,v)-> fieldPair.add(new Pair<>(k, v)));

        List<Pair<String, Object>> result = new ArrayList<>();
        for (Pair<String, Object> stringObjectPair : fieldPair) {
            try {
                Field field1 = bean.getClass().getDeclaredField(stringObjectPair.getKey());
                field1.setAccessible(true);
                Object fieldV = field1.getType().cast(stringObjectPair.getValue());
                field1.set(bean, fieldV);
            } catch (Throwable e) {
                result.add(new Pair<>(stringObjectPair.getKey(), "属性：" + stringObjectPair.getKey() + " 设置失败:" + e.getClass().toString() + ":" + e.getMessage()));
            }
        }

        if (!result.isEmpty()) {
            return result;
        }
        return "success";
    }

    /**
     * 调用bean的函数
     * <p>
     * 对于重载的函数支持度暂时只支持参数格式相同级别，其实也可以支持，只是使用方面就太复杂了
     * </p>
     *
     * @param name      bean的名字
     * @param fun       函数的名字
     * @return 函数执行的返回值
     */
    @PostMapping("call/fun")
    public Object callBeanFun(String name, String fun, @RequestBody JSONObject parameterValue) {
        Object bean = SpringBeanUtils.getBean(name);
        if (null == bean) {
            return null;
        }

        try {
            Method[] methods = bean.getClass().getDeclaredMethods();
            Method funMethod = null;
            for (Method method : methods) {
                if (!method.getName().equals(fun)) {
                    continue;
                }

                if (method.getParameters().length != 0 && null != parameterValue && method.getParameters().length != parameterValue.size()) {
                    continue;
                }
                funMethod = method;
            }

            if (null == funMethod) {
                return "没找到函数: " + fun;
            }

            Parameter[] parameters = funMethod.getParameters();
            if (parameters.length != 0 && (null == parameterValue || parameterValue.size() == 0)) {
                return "输入参数个数不满足";
            }

            List<Object> parameterValues = new ArrayList<>();
            for (int i = 0; i < parameters.length; i++) {
                parameterValues.add(ObjectUtil.cast(parameters[i].getType(), parameterValue.get("p" + (i + 1))));
            }

            funMethod.setAccessible(true);
            return funMethod.invoke(bean, parameterValues.toArray(new Object[]{}));
        } catch (Throwable e) {
            return e.getClass().toString() + ":" + e.getMessage();
        }
    }
}
