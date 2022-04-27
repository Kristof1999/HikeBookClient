package hu.kristof.nagy.hikebookclient.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.kristof.nagy.hikebookclient.data.*
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModules {
    @Binds
    abstract fun bindIAuthRepository(impl: AuthRepository): IAuthRepository

    @Binds
    abstract fun bindIRouteRepository(impl: UserRouteRepository): IUserRouteRepository

    @Binds
    abstract fun bindIWeatherRepository(impl: WeatherRepository): IWeatherRepository

    @Binds
    abstract fun bindIGroupHikeRepository(impl: GroupHikeRepository): IGroupHikeRepository
}