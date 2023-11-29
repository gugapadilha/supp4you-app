package com.guga.supp4youapp.di

import com.guga.supp4youapp.data.repository.LoginRepositoryImpl
import com.guga.supp4youapp.domain.repository.UserRepository
import com.guga.supp4youapp.domain.rest.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserRepository(retrofitService: RetrofitService): UserRepository{
        return LoginRepositoryImpl(retrofitService)
    }
}