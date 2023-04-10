package com.arnyminerz.core.serialization

import org.json.JSONObject

interface JsonSerializer <T: Any> {
    fun fromJSON(jsonObject: JSONObject): T
}
