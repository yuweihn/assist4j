package com.assist4j.boot.smtp;


import com.yuweix.assist4j.core.springboot.SmtpConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.smtp.enabled")
@Import({SmtpConf.class})
public class SmtpAutoConfiguration {

}
