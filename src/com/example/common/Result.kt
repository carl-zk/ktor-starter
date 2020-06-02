package com.example.common

/**
 * @author carl
 *
 */
class Result<T>(
    val code: Int = 200,
    val msg: String? = null,
    val data: T? = null,
    val page: Int? = null,
    val size: Int? = null
) {

    companion object Factory {
        fun success(): Result<String> {
            return Result()
        }

        fun <T> success(data: T): Result<T> {
            return Result<T>(data = data)
        }

        fun <T> success(data: T, page: Int, size: Int): Result<T> {
            return Result<T>(data = data, page = page, size = size)
        }

        fun fail(code: Int): Result<String> {
            return Result<String>(code = code)
        }

        fun fail(code: Int, msg: String): Result<String> {
            return Result(code = code, msg = msg)
        }
    }
}