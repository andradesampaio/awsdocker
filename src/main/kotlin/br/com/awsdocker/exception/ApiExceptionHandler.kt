package br.com.awsdocker.exception

import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.*
import java.util.stream.Collectors.toList


@RestControllerAdvice
class ApiExceptionHandler(val apiErrorMessageSource: MessageSource) {

    private val NO_MESSAGE_AVAILABLE: String = "No message available"
    private val  LOG =  LoggerFactory.getLogger(ApiExceptionHandler::class.java)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handlerNotValidException(exception: MethodArgumentNotValidException, locale: Locale): ResponseEntity<ErrorResponse>{

        val errors = exception.bindingResult.allErrors.stream()

        val apiErrors: List<ApiError> = errors
                .map(ObjectError::getDefaultMessage)
                .map({ code -> toApiError(code!!, locale) })
                .collect(toList())

        val errorResponse = ErrorResponse(HttpStatus.BAD_REQUEST.value(), apiErrors)
        return ResponseEntity.badRequest().body(errorResponse)

    }

    fun toApiError(code: String, locale: Locale): ApiError{
        var message: String?
        try{
            message =  apiErrorMessageSource.getMessage(code, null, locale)
        }catch (e: NoSuchMessageException){
            LOG.error("Could not find any message for ${code} code under locale")
            message = NO_MESSAGE_AVAILABLE
        }

        return ApiError(code, message)
    }
}