package fr.lawmight.cursoragents.data.security

import android.content.Context
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class EncryptedKeyStoreTest {
    private lateinit var context: Context
    private lateinit var keyStore: EncryptedKeyStore

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.deleteSharedPreferences(PREFS_FILE)
        keyStore = EncryptedKeyStore(context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE), Unit)
    }

    @After
    fun tearDown() {
        context.deleteSharedPreferences(PREFS_FILE)
    }

    @Test
    fun `roundtrip save and load stores key by sanitized label`() {
        keyStore.saveKey("work/key", "cursor-secret")

        assertEquals("cursor-secret", keyStore.loadKey("work/key"))
    }

    @Test
    fun `delete removes key and label`() {
        keyStore.saveKey(EncryptedKeyStore.DEFAULT_LABEL, "cursor-secret")

        keyStore.deleteKey(EncryptedKeyStore.DEFAULT_LABEL)

        assertNull(keyStore.loadKey(EncryptedKeyStore.DEFAULT_LABEL))
        assertEquals(emptyList<String>(), keyStore.listLabels())
    }

    @Test
    fun `list returns labels from index in insertion order`() {
        keyStore.saveKey(EncryptedKeyStore.DEFAULT_LABEL, "default-key")
        keyStore.saveKey("work/key", "work-key")

        assertEquals(listOf(EncryptedKeyStore.DEFAULT_LABEL, "work/key"), keyStore.listLabels())
    }

    @Test
    fun `hasAnyKey is false on empty store`() {
        assertFalse(keyStore.hasAnyKey())
    }

    @Test
    fun `overwrite same label replaces key without duplicating label`() {
        keyStore.saveKey(EncryptedKeyStore.DEFAULT_LABEL, "first-key")
        keyStore.saveKey(EncryptedKeyStore.DEFAULT_LABEL, "second-key")

        assertEquals("second-key", keyStore.loadKey(EncryptedKeyStore.DEFAULT_LABEL))
        assertEquals(listOf(EncryptedKeyStore.DEFAULT_LABEL), keyStore.listLabels())
        assertTrue(keyStore.hasAnyKey())
    }

    private companion object {
        const val PREFS_FILE = "cursor_keys"
    }
}
