package br.com.awsdocker.service

import br.com.awsdocker.exception.BeerAlreadyExitsException
import br.com.awsdocker.model.Beer
import br.com.awsdocker.repository.RepositoryBeers
import org.springframework.stereotype.Service

@Service
class BeerService(val repositoryBeers: RepositoryBeers) {

    fun save(beer: Beer): Beer{
        var beerByNameAndType = repositoryBeers.findByNameAndType(beer.name!!, beer.type!!)

        if (beerByNameAndType.isPresent){
            throw BeerAlreadyExitsException()
        }
        return repositoryBeers.save(beer)
    }

    fun findAll(): List<Beer> {
        return repositoryBeers.findAll()
    }
}