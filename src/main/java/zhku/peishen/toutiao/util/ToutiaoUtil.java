package zhku.peishen.toutiao.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.spi.LoggerFactory;

import java.security.MessageDigest;
import java.util.Map;

/**
 * Created by ipc on 2017/7/12.
 */
public class ToutiaoUtil {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ToutiaoUtil.class);

    public static String TOUTIAO_DOMAIN = "http://127.0.0.1:8080/";
    //图片上传路径
    public static String IMAGE_PATH = "C:/Users/ipc/Desktop/upload/";
    //验证是否为图片
    static String[] fileExt = {"png", "bmp", "jpg", "jpeg"};

    public static int midCount = 0;

    public static boolean isAllowImage(String extName){
        for(String ext : fileExt){
            if(ext.equals(extName)){
                return true;
            }
        }
        return false;
    }

    public static String getJSONString(int code){
        JSONObject json = new JSONObject();
        json.put("code",code);
        return json.toJSONString();
    }
    public static String getJSONString(int code,String msg){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return json.toJSONString();
    }
    public static String getJSONString(int code,Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        for(Map.Entry<String,Object> entry : map.entrySet()){
            json.put(entry.getKey(),entry.getValue());
        }
        return json.toJSONString();
    }

    //MD5加密
    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("生成MD5失败", e);
            return null;
        }
    }
}
