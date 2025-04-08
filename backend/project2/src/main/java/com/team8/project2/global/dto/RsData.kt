package com.team8.project2.global.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import lombok.AllArgsConstructor
import lombok.Getter

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
class RsData<Any>(
    val code: String,
    val msg: String,
    val data: Any? = null
) {
    constructor(code: String, msg: String) : this(code, msg, null)

    @get:JsonIgnore
    val statusCode: Int
        get() {
            val statusCodeStr =
                code!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            return statusCodeStr.toInt()
        }

    companion object {
        @JvmStatic
        // 성공 응답 생성 메서드 추가
        fun <Any> success(data: Any?): RsData<Any> {
            return RsData("200-1", "Success", data)
        }

        @JvmStatic
        // custom success response
        fun <Any> success(msg: String, data: Any?): RsData<Any> {
            return RsData("200-1", msg, data)
        }
    }
}
