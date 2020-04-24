package br.com.awsdocker.repository

import br.com.awsdocker.model.Beer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RepositoryBeers: JpaRepository<Beer, Long> {
}