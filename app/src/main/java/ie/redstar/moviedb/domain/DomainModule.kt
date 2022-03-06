package ie.redstar.moviedb.domain

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ie.redstar.moviedb.domain.repository.PopularMovieDataSource
import ie.redstar.moviedb.domain.repository.PopularMovieRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Singleton
    @Binds
    abstract fun providesPopularMovieRepository(
        popularMovieDataSource: PopularMovieDataSource
    ): PopularMovieRepository
}