package br.com.awsdocker.resource

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/beers")
class BeerResource {


    @GetMapping
    fun all(): List<String>{
        return Arrays.asList("Itaipava", "Colorado", "Stella Artois", "Bohemia")
    }


}