package zhku.peishen.toutiao.util;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;
import zhku.peishen.toutiao.dao.MailMessageDao;
import zhku.peishen.toutiao.model.MailMessage;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ipc on 2017/8/6.
 */
@Component
public class MailSender implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(MailSender.class);

    JavaMailSenderImpl mailSender;

    @Autowired
    MailMessageDao mailMessageDao;

    @Autowired
    private VelocityEngine velocityEngine;

    String username = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        MailMessage mailMessage = mailMessageDao.getMailMessageById(1);
        username = mailMessage.getUsername();
        String password = mailMessage.getPassword();
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setHost("smtp.163.com");

        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }

    /**
     *
     * 通过模版来包装邮件内容，并发送邮件
     * @param to
     * @param subject
     * @param template
     * @param model
     * @return
     */
    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String template, Map<String ,Object> model){
        try{
            String nick = MimeUtility.encodeText("资讯网");
            InternetAddress from = new InternetAddress(nick+"<"+username+">");
            MimeMessage mimeMessage =  mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            String result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,template,"UTF-8",model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result,true);
            mailSender.send(mimeMessage);
            return true;
        }catch(Exception e){
            logger.error("发送邮件异常"+e.getMessage());
            return false;
        }
    }
}
