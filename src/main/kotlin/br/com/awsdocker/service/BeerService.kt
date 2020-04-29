package br.com.awsdocker.service

import br.com.awsdocker.exception.BusinessException
import br.com.awsdocker.model.Beer
import br.com.awsdocker.repository.RepositoryBeers
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*


@Service
class BeerService(val repositoryBeers: RepositoryBeers) {

    fun save(beer: Beer): Beer{

        verifyIfBeerExists(beer)
        return repositoryBeers.save(beer)
    }

    fun findAll(): List<Beer> {
        return repositoryBeers.findAll()
    }

    private fun verifyIfBeerExists(beer: Beer) {
        val beerByNameAndType = repositoryBeers.findByNameAndType(beer.name, beer.type)

        if (beerByNameAndType.isPresent() && (beer.isNew() || isUpdatingToADifferentBeer(beer, beerByNameAndType))) {
            throw BusinessException("beers-5", HttpStatus.BAD_REQUEST)
        }
    }

        private fun isUpdatingToADifferentBeer(beer: Beer, beerByNameAndType: Optional<Beer>): Boolean {
            return beer.alreadyExist() && !beerByNameAndType.get().equals(beer);
        }

    fun delete(id: Long) {
        val beer = repositoryBeers.findById(id)

        if (!beer.isPresent){
            throw BusinessException("beers-6", HttpStatus.NOT_FOUND)
        }

        return repositoryBeers.deleteById(id)

    }

}

