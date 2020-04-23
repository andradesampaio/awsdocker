package br.com.awsdocker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AwsdockerApplication

fun main(args: Array<String>) {
	runApplication<AwsdockerApplication>(*args)
}
