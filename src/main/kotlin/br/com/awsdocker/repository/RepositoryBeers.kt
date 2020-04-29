package br.com.awsdocker.repository

import br.com.awsdocker.model.Beer
import br.com.awsdocker.model.BeerType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface RepositoryBeers: JpaRepository<Beer, Long> {
    fun findByNameAndType(name: String, type: BeerType): Optional<Beer>
}