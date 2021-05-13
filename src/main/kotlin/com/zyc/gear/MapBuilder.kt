@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.zyc.gear

/**
 * http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/collect/ImmutableMap.Builder.html#line.217
 */
class MapBuilder<K, V> {
  private var map: MutableMap<K, V>?
  
  constructor() {
    map = HashMap()
  }
  
  constructor(m: MutableMap<K, V>?) {
    map = m
  }
  
  fun put(k: K, v: V): MapBuilder<K, V> {
    map!![k] = v
    return this
  }
  
  fun p(k: K, v: V): MapBuilder<K, V> {
    return put(k, v)
  }
  
  fun put(e: Map.Entry<K, V>): MapBuilder<K, V> {
    return put(e.key, e.value)
  }
  
  fun p(e: Map.Entry<K, V>): MapBuilder<K, V> {
    return put(e)
  }
  
  fun putAll(m: Map<out K, V>?): MapBuilder<K, V> {
    map!!.putAll(m!!)
    return this
  }
  
  fun pa(m: Map<out K, V>?): MapBuilder<K, V> {
    return putAll(m)
  }
  
  fun remove(k: K): MapBuilder<K, V> {
    map!!.remove(k)
    return this
  }
  
  fun r(k: K): MapBuilder<K, V> {
    return remove(k)
  }
  
  fun remove(e: Map.Entry<K, V>): MapBuilder<K, V> {
    return remove(e.key)
  }
  
  fun r(e: Map.Entry<K, V>): MapBuilder<K, V> {
    return remove(e)
  }
  
  fun build(): Map<K, V>? {
    return map
  }
}