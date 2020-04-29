package br.com.awsdocker.resource

import br.com.awsdocker.model.Beer
import br.com.awsdocker.repository.RepositoryBeers
import br.com.awsdocker.service.BeerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/beers")
class BeerResource(val beerService: BeerService) {


    @GetMapping
    fun all(): List<Beer> {
        return beerService.findAll()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody beer: Beer) : Beer{
       return beerService.save(beer)

    }



}