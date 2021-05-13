@file:Suppress("unused")

package com.zyc.gear

import java.util.regex.Pattern

/**
 * @author zeng_yong_chang@163.com
 */
object Validations {
  /**
   * method:检查输入文本是否符合手机号码格式
   */
  fun isPhoneNum(input: String): Boolean {
    // TODO: zyc 改宽松一点
    return check("^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$", input)
  }
  
  fun isCaptcha(input: String): Boolean {
    return check("\\d{6}", input)
  }
  
  private fun check(regex: String, input: String): Boolean {
    val p = Pattern.compile(regex)
    val m = p.matcher(input)
    return m.matches()
  }
}