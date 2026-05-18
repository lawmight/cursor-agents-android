package fr.lawmight.cursoragents.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.lawmight.cursoragents.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface EncryptedKeyStore {
    suspend fun put(key: String)

    suspend fun get(): String?

    suspend fun clear()
}

@Singleton
class AndroidEncryptedKeyStore
    @Inject
    constructor(
        @ApplicationContext context: Context,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : EncryptedKeyStore {
        private val masterKey: MasterKey =
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        private val prefs: SharedPreferences =
            EncryptedSharedPreferences.create(
                context,
                FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )

        override suspend fun put(key: String) {
            withContext(ioDispatcher) {
                prefs.edit().putString(API_KEY, key).commitOrThrow()
            }
        }

        override suspend fun get(): String? =
            withContext(ioDispatcher) {
                prefs.getString(API_KEY, null)
            }

        override suspend fun clear() {
            withContext(ioDispatcher) {
                prefs.edit().remove(API_KEY).commitOrThrow()
            }
        }

        private fun SharedPreferences.Editor.commitOrThrow() {
            check(commit()) {
                "Failed to persist encrypted API key"
            }
        }

        companion object {
            private const val FILE_NAME = "cursor_secure_prefs"
            private const val API_KEY = "cursor_api_key"
        }
    }
