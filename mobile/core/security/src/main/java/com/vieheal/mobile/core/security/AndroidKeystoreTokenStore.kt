package com.vieheal.mobile.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.vieheal.mobile.core.model.TokenSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AndroidKeystoreTokenStore(context: Context) : SecureTokenStore {
    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override suspend fun load(): StoredTokenResult =
        withContext(Dispatchers.IO) {
            val encoded = preferences.getString(ENCRYPTED_SESSION_KEY, null)
                ?: return@withContext StoredTokenResult.Empty

            runCatching {
                val envelope = JSONObject(encoded)
                val iv = Base64.decode(envelope.getString("iv"), Base64.NO_WRAP)
                val ciphertext = Base64.decode(envelope.getString("ciphertext"), Base64.NO_WRAP)
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
                val plaintext = cipher.doFinal(ciphertext).toString(Charsets.UTF_8)
                StoredTokenResult.Available(decodeTokenSet(JSONObject(plaintext)))
            }.getOrElse {
                clearInternal()
                StoredTokenResult.Corrupted
            }
        }

    override suspend fun save(tokenSet: TokenSet) {
        withContext(Dispatchers.IO) {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            val ciphertext = cipher.doFinal(encodeTokenSet(tokenSet).toString().toByteArray())
            val envelope =
                JSONObject()
                    .put("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
                    .put("ciphertext", Base64.encodeToString(ciphertext, Base64.NO_WRAP))
            check(preferences.edit().putString(ENCRYPTED_SESSION_KEY, envelope.toString()).commit()) {
                "Protected authentication state could not be persisted"
            }
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) { clearInternal() }
    }

    private fun clearInternal() {
        preferences.edit().remove(ENCRYPTED_SESSION_KEY).commit()
        runCatching {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            if (keyStore.containsAlias(KEY_ALIAS)) keyStore.deleteEntry(KEY_ALIAS)
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(false)
                .build(),
        )
        return generator.generateKey()
    }

    private fun encodeTokenSet(tokenSet: TokenSet): JSONObject =
        JSONObject()
            .put("accessToken", tokenSet.accessToken)
            .put("refreshToken", tokenSet.refreshToken)
            .put("idToken", tokenSet.idToken)
            .put("tokenType", tokenSet.tokenType)
            .put("accessTokenExpiresAt", tokenSet.accessTokenExpiresAtEpochMillis)
            .put("refreshTokenExpiresAt", tokenSet.refreshTokenExpiresAtEpochMillis)
            .put("scope", tokenSet.scope)

    private fun decodeTokenSet(json: JSONObject): TokenSet =
        TokenSet(
            accessToken = json.getString("accessToken"),
            refreshToken = json.optString("refreshToken").takeIf { it.isNotBlank() && it != "null" },
            idToken = json.optString("idToken").takeIf { it.isNotBlank() && it != "null" },
            tokenType = json.optString("tokenType", "Bearer"),
            accessTokenExpiresAtEpochMillis = json.getLong("accessTokenExpiresAt"),
            refreshTokenExpiresAtEpochMillis =
                if (json.isNull("refreshTokenExpiresAt")) null else json.getLong("refreshTokenExpiresAt"),
            scope = json.optString("scope").takeIf { it.isNotBlank() && it != "null" },
        )

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEY_ALIAS = "vieheal.auth.session.v1"
        const val PREFERENCES_NAME = "vieheal_protected_auth"
        const val ENCRYPTED_SESSION_KEY = "encrypted_session"
    }
}
