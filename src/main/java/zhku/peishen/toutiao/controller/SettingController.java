package zhku.peishen.toutiao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ipc on 2017/7/23.
 */
@Controller
public class SettingController {

    @RequestMapping("/setting")
    @ResponseBody
    public String setting(){
        return "setting : ok";
    }
}
