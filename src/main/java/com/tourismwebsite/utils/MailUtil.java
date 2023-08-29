package com.tourismwebsite.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 发送邮件工具类
 */
public final class MailUtil {
	private MailUtil(){}
	/**
	 * 发送邮件
	 * 参数一:发送邮件给谁
	 * 参数二:发送邮件的内容
	 */
	public static void sendMail(String toEmail, String emailMsg) throws Exception {
        try {
            SimpleEmail mail = new SimpleEmail();
            mail.setHostName("smtp.qq.com");//发送邮件的服务器
            mail.setSslSmtpPort("587");
            mail.setAuthentication("847423815@qq.com","tmwqshgssseobgba");//刚刚记录的授权码，是开启SMTP的密码
            mail.setFrom("847423815@qq.com","Dennis Ritchie");  //发送邮件的邮箱和发件人
            mail.setSSLOnConnect(true); //使用安全链接
            mail.addTo(toEmail);//接收的邮箱
            //System.out.println("email"+email);
            mail.setSubject("注册验证码");//设置邮件的主题
            mail.setMsg(emailMsg);//设置邮件的内容
            mail.send();//发送
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

	/**
	 * 测试类
	 */
	public static void main(String[] args) throws Exception{
		String toEmail = "563601782@qq.com";
		sendMail(toEmail,"Welcome 奥巴马 Register TourismWebsite，点击以下连接<a href='http://localhost:8080/user?action=active&code="+UuidUtil.getUuid()+"'>激活</a>");
		System.out.println("发送成功。。。");
	}
}








