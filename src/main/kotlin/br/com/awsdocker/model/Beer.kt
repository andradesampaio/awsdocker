package br.com.awsdocker.model

import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class Beer(

        @Id
        @SequenceGenerator(name = "beer_seq", sequenceName = "beer_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "beer_seq")
        val id: Long,

        @get:NotBlank(message = "beers-1")
        var name: String,

        @get:NotNull(message = "beers-2")
        var type: BeerType,

        @get:NotNull(message = "beers-3")
        @get:DecimalMin("0", message = "beers-4")
        var volume: BigDecimal
)
