package com.zyc.gear.rxdisposableobserver

import io.reactivex.observers.DisposableMaybeObserver

/**
 * @author zeng_yong_chang@163.com
 */
abstract class MaybeHandler<T> : DisposableMaybeObserver<T>() {
  override fun onError(e: Throwable) {
    println("error occurred from rx-java DisposableMaybeObserver$e")
  }
  
  override fun onComplete() {
    println("onComplete() from rx-java DisposableMaybeObserver is called")
  }
}