package br.com.awsdocker.exception

import com.fasterxml.jackson.annotation.JsonAutoDetect
import java.util.*

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class ErrorResponse (var statusCode: Int?, var erros: List<ApiError> ) {

}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class ApiError(val code: String?, val message: String?){

}
