package br.com.awsdocker.exception

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class ErrorResponse (var statusCode: Int?, var erros: List<ApiError> ) {

}


