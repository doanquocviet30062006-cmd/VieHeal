package com.clinic.platform

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.TimeZone

@SpringBootApplication
class ClinicApplication

fun main(args: Array<String>) {

    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    runApplication<ClinicApplication>(*args)
}