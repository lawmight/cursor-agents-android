package fr.lawmight.cursoragents.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedKeyStore
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
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

        fun save(
            key: String,
            alias: String = DEFAULT_ALIAS,
        ) {
            prefs.edit().putString("key:$alias", key).apply()
            prefs.edit().putString(ACTIVE_ALIAS_KEY, alias).apply()
        }

        fun read(alias: String? = null): String? {
            val resolved =
                alias
                    ?: prefs.getString(ACTIVE_ALIAS_KEY, DEFAULT_ALIAS)
                    ?: DEFAULT_ALIAS
            return prefs.getString("key:$resolved", null)
        }

        fun aliases(): List<String> = prefs.all.keys.filter { it.startsWith("key:") }.map { it.removePrefix("key:") }

        fun activeAlias(): String? = prefs.getString(ACTIVE_ALIAS_KEY, null)

        fun setActive(alias: String) {
            prefs.edit().putString(ACTIVE_ALIAS_KEY, alias).apply()
        }

        fun remove(alias: String) {
            prefs.edit().remove("key:$alias").apply()
            if (activeAlias() == alias) {
                prefs.edit().remove(ACTIVE_ALIAS_KEY).apply()
            }
        }

        companion object {
            private const val FILE_NAME = "cursor_secure_prefs"
            private const val ACTIVE_ALIAS_KEY = "active_alias"
            const val DEFAULT_ALIAS = "default"
        }
    }
