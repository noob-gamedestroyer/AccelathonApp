package com.gamdestroyerr.accelathonapp.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplicationContext(
            @ApplicationContext context: Context
    ) = context

    @Singleton
    @Provides
    fun provideMainDispatchers() = Dispatchers.Main as CoroutineDispatcher

    @Singleton
    @Provides
    fun provideGlideInstance(
            @ApplicationContext context: Context
    ) = Glide.with(context)
            .setDefaultRequestOptions(
                    RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
            )
}

