package zhku.peishen.toutiao.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;


/**
 * Created by ipc on 2017/7/6.
 */
@Aspect
@Component
public class Log {
    private static final Logger logger = LoggerFactory.getLogger(Log.class);

    @Before("execution (* zhku.peishen.toutiao.controller.*Controller.*(..))")
    public void before(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for(Object arg :joinPoint.getArgs()){
            sb.append("arg:"+arg.toString()+"|");
        }
        logger.info("before method:"+sb.toString());
    }

    @After("execution(* zhku.peishen.toutiao.controller.*Controller.*(..))")
    public void after(){
        logger.info("after method");
    }

}
