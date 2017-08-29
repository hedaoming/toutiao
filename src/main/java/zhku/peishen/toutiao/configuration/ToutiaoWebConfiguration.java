package zhku.peishen.toutiao.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zhku.peishen.toutiao.interceptor.LoginRequiredInterceptor;
import zhku.peishen.toutiao.interceptor.PassportInterceptor;

/**
 * Created by ipc on 2017/7/16.
 * 配置过滤器
 */
@Component
public class ToutiaoWebConfiguration extends WebMvcConfigurerAdapter{

    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        //只针对路径为 /setting*的请求，才进入此interceptor
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/setting*");
        System.out.print(loginRequiredInterceptor);
        super.addInterceptors(registry);
    }
}
