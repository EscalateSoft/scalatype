package com.escalatesoft.tmap

import scala.reflect.runtime.universe._

class TMap[+T] private (private val values: List[(Type, Any)]) {
  def apply[E >: T](implicit tt: TypeTag[E]): E = {
    values.find(_._1 <:< tt.tpe).get._2.asInstanceOf[E]
  }

  def get[E](implicit tt: TypeTag[E]): Option[E] = {
    values.find(_._1 <:< tt.tpe).map(_._2.asInstanceOf[E])
  }

  def ++[S](other: TMap[S]): TMap[T with S] =
    new TMap[T with S](other.values ++ values)

  def +[S: TypeTag](other: S): TMap[T with S] =
    this.++[S](TMap[S](other))

  override def toString: String =
    "TMap(" + values.map { case (k,v) => s"$k -> $v" }.mkString(",") + ")"
}

object TMap {
  def apply[A](value: A)(implicit tt: TypeTag[A]): TMap[A] =
    new TMap[A](List(tt.tpe -> value))

  def apply[A, B](v1: A, v2: B)(implicit tt1: TypeTag[A], tt2: TypeTag[B]): TMap[A with B] =
    new TMap[A with B](List(tt1.tpe -> v1, tt2.tpe -> v2))
}
