package com.ervalsa.di

import com.ervalsa.repository.HeroRepository
import com.ervalsa.repository.HeroRepositoryImpl
import org.koin.dsl.module

val koinModule = module {
    single<HeroRepository> {
        HeroRepositoryImpl()
    }
}