package com.karigar.app.di

import com.karigar.app.data.repository.FirebaseOrderRepositoryImpl
import com.karigar.app.data.repository.OrderRepository
import com.karigar.app.data.repository.AuthRepository
import com.karigar.app.data.repository.FirebaseAuthRepositoryImpl
import com.karigar.app.data.repository.ChatRepository
import com.karigar.app.data.repository.FirebaseChatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        firebaseOrderRepositoryImpl: FirebaseOrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        firebaseChatRepositoryImpl: FirebaseChatRepositoryImpl
    ): ChatRepository
}
