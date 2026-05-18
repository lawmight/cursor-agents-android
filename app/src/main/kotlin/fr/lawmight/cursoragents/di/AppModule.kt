package fr.lawmight.cursoragents.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.lawmight.cursoragents.api.CursorApiClient
import fr.lawmight.cursoragents.data.auth.AndroidEncryptedKeyStore
import fr.lawmight.cursoragents.data.auth.EncryptedKeyStore
import fr.lawmight.cursoragents.data.repository.AgentsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

fun interface CursorApiClientFactory {
    fun create(apiKey: String): CursorApiClient
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideCursorApiClientFactory(): CursorApiClientFactory {
        return CursorApiClientFactory { apiKey -> CursorApiClient(apiKey) }
    }

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideEncryptedKeyStore(impl: AndroidEncryptedKeyStore): EncryptedKeyStore = impl

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    @Provides
    @Singleton
    fun provideAgentsRepository(
        keyStore: EncryptedKeyStore,
        clientFactory: CursorApiClientFactory,
        @ApplicationScope applicationScope: CoroutineScope,
    ): AgentsRepository {
        return AgentsRepository(
            keyStore = keyStore,
            clientFactory = clientFactory,
            scope = applicationScope,
        )
    }
}
