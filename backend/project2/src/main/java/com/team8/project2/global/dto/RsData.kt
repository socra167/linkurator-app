package com.team8.project2.global.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RsData<T>(
    val code: String,
    val msg: String,
    val data: T = Empty as T
) {
    @get:JsonIgnore
    val statusCode: Int
        get() = code.split("-")[0].toInt()

    constructor(code: String, msg: String) : this(code, msg, Empty as T)

    companion object {
        @JvmStatic
        fun <T> success(msg: String = "Success", data: T): RsData<T> {
            return RsData("200-1", msg, data)
        }

        @JvmStatic
        fun success(msg: String = "Success"): RsData<Void> {
            return RsData("200-1", msg)
        }
    }
}
