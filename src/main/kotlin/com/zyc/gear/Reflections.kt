@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.zyc.gear

import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException

/**
 * Created by "zeng_yong_chang@163.com".
 */
object Reflections {
  fun <T> getFieldValue(obj: Any, fieldName: String): T? {
    var clazz: Class<*> = obj.javaClass
    while (clazz != Any::class.java) {
      try {
        val field = clazz.getDeclaredField(fieldName)
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field[obj] as T
      } catch (e: Exception) {
        e.printStackTrace()
      }
      clazz = clazz.superclass
    }
    return null
  }
  
  fun <T> getField(fieldName: String, receiver: Any): T? {
    return try {
      getValue(receiver, fieldName) as T
    } catch (e: NoSuchFieldException) {
      e.printStackTrace()
      null
    } catch (e: IllegalAccessException) {
      e.printStackTrace()
      null
    }
  }
  
  @Throws(NoSuchMethodException::class, IllegalAccessException::class, InvocationTargetException::class)
  fun invokeMethod(
    receiver: Any,
    methodName: String,
    paramTypes: Array<Class<*>>?,
    vararg paramValues: Any
  ): Any {
    return invokeMethodInternal(receiver, receiver.javaClass, methodName, paramTypes, *paramValues)
  }
  
  @Throws(
    ClassNotFoundException::class,
    NoSuchMethodException::class,
    IllegalAccessException::class,
    InvocationTargetException::class
  )
  fun invokeStaticMethod(
    className: String?,
    methodName: String,
    paramTypes: Array<Class<*>>,
    vararg paramValues: Any
  ): Any {
    return invokeMethodInternal(
      null, Class.forName(className), methodName, paramTypes,
      *paramValues
    )
  }
  
  @Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
  fun invokeMethodInternal(
    receiver: Any?,
    clazz: Class<*>,
    methodName: String,
    paramTypes: Array<Class<*>>?,
    vararg paramValues: Any
  ): Any {
    val method =
      if (paramTypes == null)
        clazz.getDeclaredMethod(methodName)
      else
        clazz.getDeclaredMethod(methodName, *paramTypes)
    method.isAccessible = true
    return if (paramTypes == null)
      method.invoke(receiver)
    else
      method.invoke(receiver, *paramValues)
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////
  @Throws(
    ClassNotFoundException::class,
    SecurityException::class,
    NoSuchMethodException::class,
    IllegalArgumentException::class,
    InstantiationException::class,
    IllegalAccessException::class,
    InvocationTargetException::class
  )
  fun newInstance(className: String?, paramTypes: Array<Class<*>?>, paramValues: Array<Any?>): Any {
    val clazz = Class.forName(className)
    val constructor = clazz.getDeclaredConstructor(*paramTypes)
    constructor.isAccessible = true
    return constructor.newInstance(*paramValues)
  }
  
  // invoke no-argument-constructor
  @Throws(ClassNotFoundException::class, InstantiationException::class, IllegalAccessException::class)
  fun newInstance(className: String?): Any {
    val clazz = Class.forName(className)
    return clazz.newInstance()
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////
  @Throws(
    SecurityException::class,
    NoSuchFieldException::class,
    IllegalArgumentException::class,
    IllegalAccessException::class
  )
  fun setValue(instance: Any, fieldName: String, value: Any) {
    val field = getAccessibleField(instance.javaClass, fieldName)
    field[instance] = value
  }
  
  @Throws(
    SecurityException::class,
    NoSuchFieldException::class,
    IllegalArgumentException::class,
    IllegalAccessException::class
  )
  fun getValue(instance: Any, fieldName: String): Any {
    val field = getAccessibleField(instance.javaClass, fieldName)
    return field[instance]
  }
  
  @Throws(
    SecurityException::class,
    NoSuchFieldException::class,
    IllegalArgumentException::class,
    IllegalAccessException::class
  )
  fun setStaticFieldValue(clazz: Class<*>, fieldName: String, value: Any?) {
    val field = getAccessibleField(clazz, fieldName)
    field[null] = value
  }
  
  @Throws(
    SecurityException::class,
    NoSuchFieldException::class,
    IllegalArgumentException::class,
    IllegalAccessException::class
  )
  fun getStaticFieldValue(clazz: Class<*>, fieldName: String): Any {
    val field = getAccessibleField(clazz, fieldName)
    return field[null]
  }
  
  @Throws(NoSuchFieldException::class)
  fun getAccessibleField(clazz: Class<*>, fieldName: String): Field {
    val field = clazz.getDeclaredField(fieldName)
    field.isAccessible = true
    return field
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * @return all fields' name and associated value
   */
  fun getAllFieldNameAndValue(instance: Any): Map<String, Any>? {
    val map: MutableMap<String, Any> = LinkedHashMap(64)
    for (field in getAllFields(instance.javaClass)) {
      field.isAccessible = true
      try {
        map[field.name] = field[instance]
      } catch (e: IllegalAccessException) {
        e.printStackTrace()
        return null
      }
    }
    return map
  }
  
  /**
   * Return a list of all fields (whatever access status, and on whatever
   * superclass they were defined) that can be found on this class.
   * This is like a union of [Class.getDeclaredFields] which
   * ignores and super-classes, and [Class.getFields] which ignored
   * non-public fields
   *
   * @param clazz The class to introspect
   * @return The complete list of fields
   */
  fun getAllFields(clazz: Class<*>): List<Field> {
    val classes: MutableList<Class<*>> = ArrayList()
    classes.add(clazz)
    classes.addAll(getAllSuperclasses(clazz))
    return getAllFields(classes)
  }
  
  /**
   * As [.getAllFields] but acts on a list of [Class]s and
   * uses only [Class.getDeclaredFields].
   *
   * @param classes The list of classes to reflect on
   * @return The complete list of fields
   */
  fun getAllFields(classes: List<Class<*>>): List<Field> {
    val fields: MutableList<Field> = ArrayList()
    for (clazz in classes) {
      fields.addAll(listOf(*clazz.declaredFields))
    }
    return fields
  }
  
  /**
   * Return a List of super-classes for the given class.
   *
   * @param clazz the class to look up
   * @return the List of super-classes in order going up from this one
   *
   * 不包括最顶层的Object这个class !!!
   */
  fun getAllSuperclasses(clazz: Class<*>): List<Class<*>> {
    val classes: MutableList<Class<*>> = ArrayList()
    var superClass = clazz.superclass
    while (superClass != null) {
      if (!superClass.isAssignableFrom(Any::class.java)) {
        classes.add(superClass)
      }
      superClass = superClass.superclass
    }
    return classes
  }
}