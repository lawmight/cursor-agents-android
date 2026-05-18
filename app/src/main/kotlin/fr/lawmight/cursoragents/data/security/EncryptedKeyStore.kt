package fr.lawmight.cursoragents.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.lawmight.cursoragents.BuildConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedKeyStore private constructor(
    private val prefs: SharedPreferences,
) {
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : this(createEncryptedPrefs(context))

    internal constructor(
        prefs: SharedPreferences,
        @Suppress("UNUSED_PARAMETER") marker: Unit,
    ) : this(prefs)

    fun saveKey(
        label: String,
        key: String,
    ) {
        val labels = (listLabels() + label).distinct()
        prefs.edit()
            .putString(prefNameFor(label), key)
            .putString(LABELS_INDEX, Json.encodeToString(labels))
            .apply()
    }

    fun loadKey(label: String): String? = prefs.getString(prefNameFor(label), null)

    fun deleteKey(label: String) {
        val labels = listLabels().filterNot { it == label }
        prefs.edit()
            .remove(prefNameFor(label))
            .putString(LABELS_INDEX, Json.encodeToString(labels))
            .apply()
    }

    fun listLabels(): List<String> {
        val rawLabels = prefs.getString(LABELS_INDEX, null) ?: return emptyList()
        return runCatching {
            Json.decodeFromString<List<String>>(rawLabels)
        }.getOrDefault(emptyList())
    }

    fun hasAnyKey(): Boolean = listLabels().isNotEmpty()

    companion object {
        val DEFAULT_LABEL: String = BuildConfig.CURSOR_DEFAULT_KEY_LABEL
        private const val PREFS_FILE = "cursor_keys"
        private const val LABELS_INDEX = "_labels"
        private const val KEY_PREFIX = "key_"

        private fun createEncryptedPrefs(context: Context): SharedPreferences {
            val masterKey =
                MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

            return EncryptedSharedPreferences.create(
                context,
                PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }

        private fun prefNameFor(label: String): String =
            KEY_PREFIX +
                Base64.encodeToString(
                    label.toByteArray(Charsets.UTF_8),
                    Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING,
                )
    }
}
