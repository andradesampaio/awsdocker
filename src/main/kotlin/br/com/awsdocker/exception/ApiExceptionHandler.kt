package br.com.awsdocker.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
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

    @ExceptionHandler(InvalidFormatException::class)
    fun handleInvalidFormatException(exception: InvalidFormatException
                                     , locale: Locale): ResponseEntity<ErrorResponse?>? {

        val apiError = toApiError("generic-1", locale)
        val errors = mutableListOf<ApiError>(apiError)

        val errorResponse = ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handlerInternalServerError(exception: Exception, locale: Locale): ResponseEntity<ErrorResponse?>? {
        LOG.error("Error not expected", exception)
        val apiError = toApiError("error-1", locale)
        val errors = mutableListOf<ApiError>(apiError)

        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val errorResponse = ErrorResponse(status.value(), errors)
        return ResponseEntity.status(status).body(errorResponse)
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(exception: BusinessException, locale: Locale?): ResponseEntity<ErrorResponse?>? {

        val apiError = toApiError(exception.code, locale!!)
        val errors = mutableListOf<ApiError>(apiError)

        val errorResponse = ErrorResponse(exception.status.value(), errors)

        return ResponseEntity.badRequest().body(errorResponse)
    }

    fun toApiError(code: String, locale: Locale): ApiError {
        var message: String
        try{
            message =  apiErrorMessageSource.getMessage(code, null, locale)
        }catch (e: NoSuchMessageException){
            LOG.error("Could not find any message for ${code} code under locale")
            message = NO_MESSAGE_AVAILABLE
        }

        return ApiError(code, message)
    }





}