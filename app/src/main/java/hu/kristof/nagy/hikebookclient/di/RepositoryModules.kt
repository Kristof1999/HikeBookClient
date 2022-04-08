package hu.kristof.nagy.hikebookclient.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.kristof.nagy.hikebookclient.data.AuthRepository
import hu.kristof.nagy.hikebookclient.data.IAuthRepository
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModules {
    @Binds
    abstract fun bindIAuthRepository(impl: AuthRepository): IAuthRepository

    @Binds
    abstract fun bindIRouteRepository(impl: UserRouteRepository): IUserRouteRepository
}