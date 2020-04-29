package br.com.awsdocker.service

import br.com.awsdocker.exception.BeerAlreadyExitsException
import br.com.awsdocker.model.Beer
import br.com.awsdocker.model.BeerType
import br.com.awsdocker.repository.RepositoryBeers
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockitoExtension::class)
class BeerServiceTest {

    @Mock
    lateinit var repositoryBeers: RepositoryBeers

    @InjectMocks
    lateinit var service: BeerService

    @Test
    fun `should deny creation of beer that exists`(){
        val beerInDataBase = Beer(1L,"Heineken", BeerType.LAGER, BigDecimal(355))
        whenever(repositoryBeers.findByNameAndType("Heineken", BeerType.LAGER)) doReturn (Optional.of(beerInDataBase))

        val newBeer = Beer(1L,"Heineken", BeerType.LAGER, BigDecimal(355))

        assertThrows<BeerAlreadyExitsException> { service.save(newBeer) }

    }

    @Test
    fun `should_create_new_beer`(){
        val newBeer = Beer(0,"Skol", BeerType.IPA, BigDecimal(600))
        whenever(repositoryBeers.findByNameAndType("Skol", BeerType.IPA)) doReturn (Optional.empty())

        val newSaved = Beer(1L,"Skol", BeerType.IPA, BigDecimal(600))
        whenever(repositoryBeers.save(newBeer)) doReturn (newSaved)

        val beer = service.save(newBeer)

        Assertions.assertEquals(newSaved, beer)

    }
}