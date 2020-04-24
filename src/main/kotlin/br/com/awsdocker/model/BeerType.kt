package br.com.awsdocker.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class BeerType{
    @JsonProperty("type")
    LAGER,
    @JsonProperty("type")
    PILSEN,
    @JsonProperty("type")
    IPA
}