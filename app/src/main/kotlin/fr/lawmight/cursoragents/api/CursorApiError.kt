package fr.lawmight.cursoragents.api

sealed class CursorApiError(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    data object Unauthorized : CursorApiError("Invalid or expired Cursor API key.")

    data object Forbidden : CursorApiError("This API key cannot access that resource.")

    data object NotFound : CursorApiError("Cursor API resource was not found.")

    data class RateLimited(val retryAfterSec: Int?) :
        CursorApiError("Cursor API rate limit exceeded.")

    data class ServerError(val code: Int) :
        CursorApiError("Cursor API returned server error HTTP $code.")

    data class NetworkError(override val cause: Throwable) :
        CursorApiError("Network error while calling Cursor API.", cause)

    data class DecodeError(override val cause: Throwable) :
        CursorApiError("Failed to decode Cursor API response.", cause)
}
