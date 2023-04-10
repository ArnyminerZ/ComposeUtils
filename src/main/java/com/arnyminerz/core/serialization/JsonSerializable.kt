package com.arnyminerz.core.serialization

import org.json.JSONObject

interface JsonSerializable {
    fun toJSON(): JSONObject
}