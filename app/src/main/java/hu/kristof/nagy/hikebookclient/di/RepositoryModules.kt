package hu.kristof.nagy.hikebookclient.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.kristof.nagy.hikebookclient.data.repository.*
import hu.kristof.nagy.hikebookclient.data.repository.routes.GroupRouteRepository
import hu.kristof.nagy.hikebookclient.data.repository.routes.IGroupRouteRepository
import hu.kristof.nagy.hikebookclient.data.repository.routes.IUserRouteRepository
import hu.kristof.nagy.hikebookclient.data.repository.routes.UserRouteRepository

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
    abstract fun bindIUserRouteRepository(impl: UserRouteRepository): IUserRouteRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class GroupRouteRepositoryModule {
    @Binds
    abstract fun bindIGroupRouteRepository(impl: GroupRouteRepository): IGroupRouteRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class GroupsRepositoryModule {
    @Binds
    abstract fun bindIGroupsRepository(impl: GroupsRepository): IGroupsRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class GroupHikeRepositoryModule {
    @Binds
    abstract fun bindIGroupHikeRepository(impl: GroupHikeRepository): IGroupHikeRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class HikeRepositoryModule {
    @Binds
    abstract fun bindIHikeRepository(impl: HikeRepository): IHikeRepository
}