package com.escalatesoft.geometry

import com.escalatesoft.crs.CRSDef.CRSDefinitions._
import org.scalatest.{FunSpec, Matchers}

class PolygonSpec extends FunSpec with Matchers {
  describe ("Point in Polygon with CRSs") {
    it ("should be true only when a point is in the polygon for the same CRS") {
      val poly = Polygon[EPSG_32615](
        (0.0, 0.0), (0.0, 10.0), (10.0, 10.0), (10.0, 0.0), (0.0, 0.0)
      )

      poly.contains(Coord2D[EPSG_32615](5.0, 5.0)) should be (true)
      poly.contains(Coord2D[EPSG_32615](0.001, 0.001)) should be (true)
      poly.contains(Coord2D[EPSG_32615](9.999, 9.999)) should be (true)

      // should also work with within on the coord
      Coord2D[EPSG_32615](5.0, 5.0).within(poly) should be (true)
      Coord2D[EPSG_32615](0.001, 0.001).within(poly) should be (true)
      Coord2D[EPSG_32615](9.999, 9.999).within(poly) should be (true)

      // should be false when outside of the poly
      poly.contains(Coord2D[EPSG_32615](15.0, 15.0)) should be (false)
      poly.contains(Coord2D[EPSG_32615](-15.0, -15.0)) should be (false)
      poly.contains(Coord2D[EPSG_32615](0.0, 0.0)) should be (false)
      poly.contains(Coord2D[EPSG_32615](10.0, 10.0)) should be (false)

      // and using within
      // should be false when outside of the poly
      Coord2D[EPSG_32615](15.0, 15.0).within(poly) should be (false)
      Coord2D[EPSG_32615](-15.0, -15.0).within(poly) should be (false)
      Coord2D[EPSG_32615](0.0, 0.0).within(poly) should be (false)
      Coord2D[EPSG_32615](10.0, 10.0).within(poly) should be (false)
    }

    it ("should not even compile if the wrong CRSs are used") {
      val poly = Polygon[EPSG_32615](
        (0.0, 0.0), (0.0, 10.0), (10.0, 10.0), (10.0, 0.0), (0.0, 0.0)
      )

      "poly.contains(Coord2D[EPSG_32616](5.0, 5.0))" shouldNot compile
      "poly.contains(Coord2D[EPSG_32616](5.0, 5.0))" shouldNot typeCheck

      //poly.contains(Coord2D[EPSG_32616](5.0, 5.0))
    }
  }

  describe ("Area of a polygon") {
    it ("should return the area of a polygon correctly") {
      val poly = Polygon[EPSG_32615](
        (0.0, 0.0), (0.0, 10.0), (10.0, 10.0), (10.0, 0.0), (0.0, 0.0)
      )
      Polygon.area(poly) should be (100.0 +- 1e-6)
    }

    it ("should work on an angle-based CRS polygon also") {
      val poly = Polygon[EPSG_4326](
        (0.0, 0.0), (0.0, 10.0), (10.0, 10.0), (10.0, 0.0), (0.0, 0.0)
      )
      Polygon.area(poly) should be (100.0 +- 1e-6)
    }

    it ("should work only for meter based CRSs with Polygon.areaSquareMeters") {
      val poly1 = Polygon[EPSG_32615](
        (0.0, 0.0), (0.0, 10.0), (10.0, 10.0), (10.0, 0.0), (0.0, 0.0)
      )

      val poly2 = Polygon[EPSG_4326](
        (0.0, 0.0), (0.0, 10.0), (10.0, 10.0), (10.0, 0.0), (0.0, 0.0)
      )

      Polygon.areaSquareMeters(poly1) should be (100.0 +- 1e-6)

      // Polygon.areaSquareMeters(poly2) should be (100.0 +- 1e-6)

      "Polygon.areaSquareMeters(poly2)" shouldNot compile
      "Polygon.areaSquareMeters(poly2)" shouldNot typeCheck
    }
  }
}
