package br.com.awsdocker.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource

@Configuration
class ApiErrorConfig {

    fun apiErrorMessageSource(): MessageSource{
        var messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("classpath:/api_errors")
        messageSource.setDefaultEncoding("UTF-8")
        return messageSource
    }
}