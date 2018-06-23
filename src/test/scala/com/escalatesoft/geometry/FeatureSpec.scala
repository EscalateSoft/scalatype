package com.escalatesoft.geometry

import com.escalatesoft.crs.CRSType
import com.escalatesoft.crs.CRSType.CRSDefinitions._
import com.escalatesoft.tmap.TMap
import org.scalatest.{FunSpec, Matchers}

class FeatureSpec extends FunSpec with Matchers {
  describe ("A point geometry with Int data") {
    it ("should work as long as all types are consistent") {
      val feature: Feature[EPSG_32615, Coord2D[EPSG_32615], Int] =
        Feature(Coord2D(5.0, 5.0), 20)

      feature.geometry should be(Coord2D[EPSG_32615](5.0, 5.0))
      feature.attributes * 2 should be(40)
    }

    it ("should not compile if the CRSs are different types") {
      "Feature[EPSG_32616, Coord2D[EPSG_32615], Int](Coord2D[EPSG_32615](5.0, 5.0), 20)" shouldNot typeCheck
    }

    it ("should allow a function to be mapped over the attributes") {
      val feature: Feature[EPSG_32615, Coord2D[EPSG_32615], List[String]] =
        Feature(Coord2D[EPSG_32615](5.0, 5.0), List("three", "strings"))

      val feature2: Feature[EPSG_32615, Coord2D[EPSG_32615], List[Int]] =
        feature.mapAttributes(attr => attr.map(_.length))
    }

    it ("should allow covariant behavior in the attributes") {
      val feature: Feature[EPSG_32615, Coord2D[EPSG_32615], List[Int]] =
        Feature(Coord2D[EPSG_32615](5.0, 5.0), List(1, 2, 3))
      countAttributes(feature) should be (3)

      val feature2: Feature[EPSG_32615, Coord2D[EPSG_32615], List[String]] =
        Feature(Coord2D[EPSG_32615](5.0, 5.0), List("hello", "world"))
      countAttributes(feature2) should be (2)

      val feature3: Feature[EPSG_32615, Coord2D[EPSG_32615], Int] =
        Feature(Coord2D[EPSG_32615](5.0, 5.0), 20)

      "countAttributes(feature3)" shouldNot typeCheck
    }

    it ("should allow a map of attributes to have attributes added creating a new feature") {
      val feature: Feature[EPSG_32615, Coord2D[EPSG_32615], Map[String, Int]] =
        Feature(Coord2D(5.0, 5.0), Map("foo" -> 2, "bar" -> 3))

      val feature2 = feature.mapAttributes(_ + ("baz" -> 4))

      feature.attributes should be (Map("foo" -> 2, "bar" -> 3))
      feature2.attributes should be (Map("foo" -> 2, "bar" -> 3, "baz" -> 4))

      feature.geometry should be (feature2.geometry)
    }

    it ("should allow a type-map attribute to accumulate types") {
      val feature: Feature[EPSG_32615, Coord2D[EPSG_32615], TMap[FieldName]] =
        Feature(Coord2D(5.0, 5.0), TMap(FieldName("Yarick 80")))

      feature.attributes[FieldName].name should be ("Yarick 80")
      "feature.attributes[Flow].amnt" shouldNot typeCheck
      "feature.attributes[Duration].amnt" shouldNot typeCheck

      val featureWithFlowAndDuration: Feature[EPSG_32615, Coord2D[EPSG_32615], TMap[FieldName with Flow with Duration]] =
        feature.mapAttributes(_ + Flow(10.6)).mapAttributes(_ + Duration(2.5))

      featureWithFlowAndDuration.attributes[FieldName].name should be ("Yarick 80")
      featureWithFlowAndDuration.attributes[Flow].amnt should be (10.6 +- 1e-9)
      featureWithFlowAndDuration.attributes[Duration].amnt should be (2.5 +- 1e-9)

      val featureWithYield = calcYield(featureWithFlowAndDuration)

      featureWithYield.attributes.amnt should be (26.5 +- 1e-9)


      val featureWithYield2: Feature[EPSG_32615, Coord2D[EPSG_32615], TMap[FieldName with Yield]] =
        feature.mapAttributes(_ + Yield(26.5))

      "calcYield(featureWithYield2)" shouldNot typeCheck
    }

    it ("should allow addition of type attributes with pretty syntax") {
      val feature: Feature[EPSG_32615, Coord2D[EPSG_32615], TMap[FieldName]] =
        Feature(Coord2D(5.0, 5.0), TMap(FieldName("Yarick 80")))

      val featureWithFlowAndDuration: Feature[EPSG_32615, Coord2D[EPSG_32615], TMap[FieldName with Flow with Duration]] =
        feature.
          addAttr(Flow(10.6)).
          addAttr(Duration(2.5))

      featureWithFlowAndDuration.attributes[FieldName] should be (FieldName("Yarick 80"))
      featureWithFlowAndDuration.attributes[Flow] should be (Flow(10.6))
      featureWithFlowAndDuration.attributes[Duration] should be (Duration(2.5))
    }

    it ("should not allow addition of type attributes to something without a TMap") {
      val feature: Feature[EPSG_32615, Coord2D[EPSG_32615], FieldName] =
        Feature(Coord2D(5.0, 5.0), FieldName("Yarick 80"))

      "feature.addAttr(Flow(10.6))" shouldNot typeCheck
    }
  }

  def countAttributes(feature: Feature[_, _, List[Any]]): Int = feature.attributes.size

  def calcYield[CRS: CRSType](feature: Feature[CRS, Coord2D[CRS], TMap[Flow with Duration]]): Feature[CRS, Coord2D[CRS], Yield] = {
    Feature(feature.geometry, Yield(feature.attributes[Flow].amnt * feature.attributes[Duration].amnt))
  }
}

case class Duration(amnt: Double)
case class Flow(amnt: Double)
case class FieldName(name: String)
case class Yield(amnt: Double)

