package hu.kristof.nagy.hikebookclient.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.kristof.nagy.hikebookclient.data.AuthRepository
import hu.kristof.nagy.hikebookclient.data.IAuthRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {
    @Binds
    abstract fun bindIAuthRepository(impl: AuthRepository): IAuthRepository
}