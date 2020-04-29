package br.com.awsdocker.exception

import org.springframework.http.HttpStatus

data class BusinessException(val code: String, val status: HttpStatus): RuntimeException() {
}