@file:Suppress("unused")

package com.zyc.gear

import org.apache.commons.lang3.builder.ReflectionToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

/**
 * created by zeng_yong_chang@163.com
 */
object ReflectLogs {
  /**
   * @param reflectUpToClass 从子类到基类逐级打印成员时，要停止在哪一级。举例：类C继承子类B，类B继承子类A，<br></br>
   * 当以B.class作为参数时，输出的就是类C和类B中定义的成员，不会输入类A的成员。
   */
  fun <T> toStringWithMultiLine(obj: T, reflectUpToClass: Class<in T>?): String {
    return ReflectionToStringBuilder.toString(
      obj,
      ToStringStyle.MULTI_LINE_STYLE,
      false,
      false,
      reflectUpToClass
    )
  }
}