package hu.kristof.nagy.hikebookclient.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.kristof.nagy.hikebookclient.data.*
import hu.kristof.nagy.hikebookclient.data.routes.GroupRouteRepository
import hu.kristof.nagy.hikebookclient.data.routes.IGroupRouteRepository
import hu.kristof.nagy.hikebookclient.data.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.data.routes.UserRouteRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModules {
    @Binds
    abstract fun bindIAuthRepository(impl: AuthRepository): IAuthRepository

    @Binds
    abstract fun bindIWeatherRepository(impl: WeatherRepository): IWeatherRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRouteRepositoryModule {
    @Binds
    abstract fun bindIRouteRepository(impl: UserRouteRepository): IUserRouteRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class GroupRouteRepositoryModule {
    @Binds
    abstract fun bindIGroupRepository(impl: GroupRouteRepository): IGroupRouteRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class GroupHikeRepositoryModule {
    @Binds
    abstract fun bindIGroupHikeRepository(impl: GroupHikeRepository): IGroupHikeRepository
}