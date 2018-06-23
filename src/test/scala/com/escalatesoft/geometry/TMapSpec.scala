package com.escalatesoft.geometry

import com.escalatesoft.tmap.TMap
import org.scalatest.{FunSpec, Matchers}

class TMapSpec extends FunSpec with Matchers {

  describe ("A type indexed map") {
    it ("should be easily creatable with inferred type") {
      val tmap: TMap[Int] = TMap(10)
      tmap[Int] should be (10)

      // tmap[String] should be (empty)
      "tmap[String]" shouldNot typeCheck
    }

    it ("should accumulate items and types as they are added") {
      val tmap1: TMap[Int] = TMap(10)
      val tmap2: TMap[Int with String] = tmap1 + "hello"
      val tmap3: TMap[Int with String with List[Int]] = tmap2 + List(1,2,3)

      tmap3[Int] should be (10)
      tmap3[String] should be ("hello")
      tmap3[List[Int]] should be (List(1,2,3))
      "tmap3[List[String]]" shouldNot typeCheck
    }

    def calc(attrs: TMap[List[Int] with Int]): List[Int] = {
      val list = attrs[List[Int]]
      val mult = attrs[Int]

      list.map(_ * mult)
    }

    it ("should allow a function to be called that only needs some of the types") {
      val tmap1: TMap[Int] = TMap(10)
      val tmap2: TMap[Int with String] = tmap1 + "hello"
      val tmap3: TMap[Int with String with List[Int]] = tmap2 + List(1,2,3)

      calc(tmap3) should be (List(10, 20, 30))
      "calc(tmap2)" shouldNot typeCheck
    }
  }

}
