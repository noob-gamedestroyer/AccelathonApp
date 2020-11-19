package com.gamdestroyerr.accelathonapp.di

import com.gamdestroyerr.accelathonapp.repositories.DefaultMainRepository
import com.gamdestroyerr.accelathonapp.repositories.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object MainModule {

    @ActivityScoped
    @Provides
    fun provideAuthRepository() = DefaultMainRepository() as MainRepository
}