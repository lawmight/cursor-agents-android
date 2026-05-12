package fr.lawmight.cursoragents.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.lawmight.cursoragents.data.api.CursorApiClient
import fr.lawmight.cursoragents.data.auth.EncryptedKeyStore
import fr.lawmight.cursoragents.data.repository.AgentsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAgentsRepository(keyStore: EncryptedKeyStore): AgentsRepository =
        AgentsRepository { alias -> keyStore.read(alias)?.let(::CursorApiClient) }
}
