package com.zw.javaoperationoftencentcloudsmsservice.controller;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.zw.javaoperationoftencentcloudsmsservice.VO.Sms;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Date:2023-05-29-9:30
 * @author:Esan
 */
@RequestMapping("/sms")
@RestController
public class SmsController {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    //验证码过期时间
    private Integer expireTime=5;

    @RequestMapping(value="/send",method = RequestMethod.POST)
    public void sendSms(@RequestBody Sms sms){
        //你的SDKAppID(在应用列表中查看)
        int appid = ;
        //你的App Key(在应用列表详情中查看)
        String appKey = "";
        //你的短信模板id(在国内短信-》正文模板管理中查看)
        int templateId = ;
        //你的签名内容(在国内短信-》签名管理中查看)
        String smsSign = "";
        //生成六位随机数，作为验证码
        String randomCode = RandomStringUtils.randomNumeric(6);
        //打印验证码
        System.out.println(randomCode);
        //将验证码存入redis,key为手机号，value为验证码
        redisTemplate.opsForValue().set(sms.getPhoneNumber(),randomCode,expireTime, TimeUnit.MINUTES);
        //设置调用腾讯云短信服务的参数,注意这个参数和你在腾讯云控制台中定义的短信模板中的参数要一致
        String[] params = {randomCode,Integer.toString(expireTime)};
        SmsSingleSender smsSingleSender = new SmsSingleSender(appid,appKey);
        try {
            //发送验证码
            SmsSingleSenderResult result = smsSingleSender.sendWithParam("86",sms.getPhoneNumber(),templateId,params,smsSign,"","");
            System.out.println(result);
        } catch (HTTPException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}