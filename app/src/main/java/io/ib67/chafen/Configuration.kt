package io.ib67.chafen

import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object Config {
    var userToken by StringConfigProp
    var userName by StringConfigProp
}

private val mmkv: MMKV
    get() = MMKV.defaultMMKV();

internal object StringConfigProp : ReadWriteProperty<Config, String> {
    override fun getValue(thisRef: Config, property: KProperty<*>): String {
        return mmkv.decodeString(property.name) ?: ""
    }

    override fun setValue(thisRef: Config, property: KProperty<*>, value: String) {
        mmkv.encode(property.name, value)
    }

}