package fr.lawmight.cursoragents.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedKeyStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun save(key: String, alias: String = DEFAULT_ALIAS) {
        prefs.edit().putString("key:\$alias", key).apply()
        prefs.edit().putString("active_alias", alias).apply()
    }

    fun read(alias: String? = null): String? {
        val a = alias ?: prefs.getString("active_alias", DEFAULT_ALIAS) ?: DEFAULT_ALIAS
        return prefs.getString("key:\$a", null)
    }

    fun aliases(): List<String> =
        prefs.all.keys.filter { it.startsWith("key:") }.map { it.removePrefix("key:") }

    fun activeAlias(): String? = prefs.getString("active_alias", null)

    fun setActive(alias: String) {
        prefs.edit().putString("active_alias", alias).apply()
    }

    fun remove(alias: String) {
        prefs.edit().remove("key:\$alias").apply()
        if (activeAlias() == alias) {
            prefs.edit().remove("active_alias").apply()
        }
    }

    companion object {
        private const val FILE_NAME = "cursor_secure_prefs"
        const val DEFAULT_ALIAS = "default"
    }
}
