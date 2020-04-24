package br.com.awsdocker.exception

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class ApiError(val code: String?, val message: String?){

}