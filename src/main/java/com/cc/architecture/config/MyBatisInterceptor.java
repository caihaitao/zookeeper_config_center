package com.cc.architecture.config;

import com.cc.architecture.annotation.FiledValue;
import com.cc.architecture.util.BeanNameUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

/**
 * TODO 类的描述
 *
 * @author cc
 * @createTime 2018-05-24 10:42:38
 */
@Intercepts({
        @Signature(method = "update",type = Executor.class,args ={MappedStatement.class,Object.class} ),
        @Signature(method = "query",type = Executor.class,args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class MyBatisInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisInterceptor.class);
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] invocationArgs = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) invocationArgs[0];
        final SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object params = null;
        if(invocationArgs.length > 1) {
            params = invocationArgs[1];
        }
        final BoundSql boundSql = mappedStatement.getBoundSql(params);
        final String sql = boundSql.getSql();
        final String name = invocation.getMethod().getName();
        logger.info("method name -{},execute sql :{},params:{}",name,sql,params);
        final Object result = invocation.proceed();
        if(SqlCommandType.SELECT == sqlCommandType) {
            if(result instanceof List) {
                final List result1 = (List) result;
                for(Object o : result1) {
                    final Field[] declaredFields = o.getClass().getDeclaredFields();
                    for(Field field : declaredFields) {
                        final FiledValue filedValue = field.getAnnotation(FiledValue.class);
                        if(filedValue != null) {
                            final String sourceClassName = filedValue.sourceClassName();
                            final String sourceMethod = filedValue.sourceMethod();
                            final String bindProperty = filedValue.bindProperty();
                            String parm = filedValue.param();
                            final Object p = o.getClass().getMethod("get" + StringUtils.capitalize(parm)).invoke(o, null);
                            final Object bean = BeanNameUtil.getBean(sourceClassName, Object.class);
                            logger.info("{} has methods {}",bean,bean.getClass().getMethods().toString());
                            final Method method = bean.getClass().getDeclaredMethod(sourceMethod,String.class);

                            method.setAccessible(true);
                            final Object rs = method.invoke(bean, p.toString());
                            final Object source = rs.getClass().getMethod("get" + StringUtils.capitalize(bindProperty)).invoke(rs, null);
                            field.setAccessible(true);
                            field.set(o,source);
                        }
                    }
                }
                /*((List) result).stream().forEach(res -> {
                    final Field[] declaredFields = res.getClass().getDeclaredFields();
                    Arrays.stream(declaredFields).forEach(field -> {
                        final FiledValue filedValue = field.getAnnotation(FiledValue.class);
                        if(filedValue != null) {
                            final String sourceClassName = filedValue.sourceClassName();
                            final String sourceMethod = filedValue.sourceMethod();
                            final Class bindClass = filedValue.bindClass();
                            final String bindProperty = filedValue.bindProperty();
                            final Object bean = beanNameUtil.getBean(sourceClassName);
                        }
                    });
                });*/

            } else {

            }
        }
        return result;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
