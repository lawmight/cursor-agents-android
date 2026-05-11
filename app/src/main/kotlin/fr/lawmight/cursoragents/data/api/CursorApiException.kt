package fr.lawmight.cursoragents.data.api

sealed class CursorApiException(message: String) : RuntimeException(message) {
    object Unauthorized : CursorApiException("Invalid or expired Cursor API key.")

    object Forbidden : CursorApiException("This API key can't access that resource.")

    object NotFound : CursorApiException("Not found.")

    object RateLimited : CursorApiException("Rate limit hit. Try again in a bit.")

    data class Unexpected(val code: Int, val rawBody: String) :
        CursorApiException("Cursor API returned HTTP $code: $rawBody")
}
