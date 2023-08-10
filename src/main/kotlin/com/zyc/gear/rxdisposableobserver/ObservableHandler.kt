package com.zyc.gear.rxdisposableobserver

import io.reactivex.observers.DisposableObserver

/**
 * @author zeng_yong_chang@163.com
 */
abstract class ObservableHandler<T> : DisposableObserver<T>() {
  override fun onError(e: Throwable) {
    e.printStackTrace()
    println("error occurred from rx-java DisposableObserver: $e")
  }
  
  override fun onComplete() {
    println("onComplete() from rx-java DisposableObserver is called")
  }
}