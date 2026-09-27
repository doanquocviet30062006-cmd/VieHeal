package com.vieheal.mobile.core.security

import com.vieheal.mobile.core.model.TokenSet

sealed interface StoredTokenResult {
    data object Empty : StoredTokenResult
    data class Available(val tokenSet: TokenSet) : StoredTokenResult
    data object Corrupted : StoredTokenResult
}

interface SecureTokenStore {
    suspend fun load(): StoredTokenResult
    suspend fun save(tokenSet: TokenSet)
    suspend fun clear()
}
