package fr.lawmight.cursoragents.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.lawmight.cursoragents.data.api.CursorApiClient
import fr.lawmight.cursoragents.data.auth.EncryptedKeyStore
import fr.lawmight.cursoragents.data.repository.AgentsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

fun interface CursorApiClientFactory {
    fun create(apiKey: String): CursorApiClient
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideCursorApiClientFactory(): CursorApiClientFactory = CursorApiClientFactory { apiKey -> CursorApiClient(apiKey) }

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideAgentsRepository(
        keyStore: EncryptedKeyStore,
        clientFactory: CursorApiClientFactory,
    ): AgentsRepository = AgentsRepository { alias -> keyStore.read(alias)?.let(clientFactory::create) }
}
