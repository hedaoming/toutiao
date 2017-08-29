package zhku.peishen.toutiao.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zhku.peishen.toutiao.util.ToutiaoUtil;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by ipc on 2017/7/31.
 * 七牛云上传服务
 */
@Service
public class QiniuService {

    private static Logger logger = LoggerFactory.getLogger(QiniuService.class);
    //1. 配置七牛云
    //1.1 构造一个带指定Zone对象的配置类
    Configuration cfg = new Configuration(Zone.autoZone());
    //1.2 上传管理
    UploadManager uploadManager = new UploadManager(cfg);
    //1.3 上传凭证
    String accessKey = "9nJ1_4hEvEeM3HMDfg5_rD8ZJkTWeTILxwk_BwvR";
    String secretKey = "t_hftDL41RFA6K542vefiebAAN1CzbGE3lOQ4hUK";
    //1.4 指定存储空间
    String bucket = "larve";
    //1.5 创建认证
    Auth auth = Auth.create(accessKey, secretKey);
    //1.6 简单上传，使用默认策略，只需要设置上传的空间名就可以了
    String upToken = auth.uploadToken(bucket);


    public static String QINIU_IMAGE_DOMAIN = "http://otxp9uiz6.bkt.clouddn.com/";
    public String uploadImage(MultipartFile file) throws IOException{
        //2. 校验
        //2.1 是否是图片
        int pos = file.getOriginalFilename().lastIndexOf(".");
        if(pos<0){
            return null;
        }
        //2.2 文件后缀名
        String fileExt = file.getOriginalFilename().substring(pos+1).toLowerCase();
        if(!ToutiaoUtil.isAllowImage(fileExt)){
            return null;
        }


        String fileName = UUID.randomUUID().toString().replace("-","")+"."+fileExt;
        //3. 保存到服务器
        try {
            //3.1 上传
            Response response = uploadManager.put(file.getBytes(), fileName, upToken);
            //3.3 返回路径
            if (response.isJson() && response.isOK()){
                //key对应的就是文件名
                return QINIU_IMAGE_DOMAIN+ JSONObject.parseObject(response.bodyString()).get("key");
            }else{
                logger.error("七牛云异常"+response.bodyString());
                return null;
            }
        } catch (QiniuException ex) {
            logger.error("七牛云上传错误"+ex.getMessage());
            return null;
        }
    }

}
