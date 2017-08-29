package zhku.peishen.toutiao.controller;

import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import zhku.peishen.toutiao.model.User;
import zhku.peishen.toutiao.service.IndexService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by ipc on 2017/7/5.
 */
//@Controller
public class IndexController {

    @RequestMapping(path = {"/index","/"})
    @ResponseBody
    public String index(HttpSession session){
        return "Index page"+session.getAttribute("msg");
    }

    @RequestMapping(value = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") String userId,
                          @RequestParam(value="type",defaultValue = "1") int type,
                          @RequestParam(value="key",defaultValue="newCoder") String key){

        return String.format("GroupId{%s},UserId{%s},Type{%d},Key{%s}",groupId,userId,type,key);
    }


    @RequestMapping("/news")
    public String news(Model model){
        model.addAttribute("value","val");

        List list = Arrays.asList(new String[]{"red","black","yellow"});
        Map<Integer ,Integer> map = new HashMap<>();
        for(int i = 0;i<4;i++){
            map.put(i,i*i);
        }

        model.addAttribute("list",list);
        model.addAttribute("map",map);
        model.addAttribute("user",new User("mars"));
        return "news";
    }

    @RequestMapping("/request")
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> names = request.getHeaderNames();

        while(names.hasMoreElements()){
            String name = names.nextElement();
            sb.append(name+":"+request.getHeader(name)+"<br>");
        }

        for(Cookie cookie:request.getCookies()){
            sb.append("cookieName = "+cookie.getName()+"<br>");
            sb.append("cookieValue = "+cookie.getValue());
            sb.append("<br>");
        }
        sb.append("getMethod = "+request.getMethod()+"<br>");
        sb.append("getPathInfo="+request.getPathInfo()+"<br>");
        sb.append("getQueryString="+request.getQueryString()+"<br>");
        sb.append("getRequestURI="+request.getRequestURI()+"<br>");
        return sb.toString();

    }

    @RequestMapping("/response")
    @ResponseBody
    public String response(@CookieValue(value = "newId",defaultValue="0") String newId,
                           @RequestParam(value="key",defaultValue = "key") String key,
                           @RequestParam(value="value",defaultValue = "value") String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key,value));
        response.addHeader(key,value);
        return "newId from cookie:"+newId;
    }

    @RequestMapping(value = {"/redirect/{code}"})
    public RedirectView redirect(@PathVariable("code") int code){
        RedirectView red = new RedirectView("/",true);
        if(code == 301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }

    @RequestMapping(value = "/redirectTwo/{code}")
    public String redirectTwo(@PathVariable("code") String code,
                              HttpSession session){
        session.setAttribute("msg","jump into the session");
        return "redirect:/";
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key",defaultValue = "a") String key){
        if("admin".equals(key)){
            return "admin login";
        }
        throw new IllegalArgumentException("key 错误");
    }

    @ExceptionHandler
    @ResponseBody
    public String error(Exception e){
        return e.getMessage();
    }

    @Autowired
    private IndexService indexService;
    //IoC
    @RequestMapping("/iocService")
    @ResponseBody
    public String iocTest(){
        return "iocTest,"+indexService.say();
    }
}
