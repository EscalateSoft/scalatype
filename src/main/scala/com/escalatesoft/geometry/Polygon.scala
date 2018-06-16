package com.escalatesoft.geometry

import com.escalatesoft.crs.{CRSType, MeterCRSType}
import com.vividsolutions.jts.geom
import com.vividsolutions.jts.geom.{Coordinate, GeometryFactory, LinearRing, Polygon => JTSPolygon}
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence

import collection.JavaConverters._

case class Polygon[CRS: CRSType](coords: Vector[Coord2D[CRS]]) extends GeometryType[CRS] {

  private val closed: Seq[Coord2D[CRS]] =
    if (coords.last == coords.head) coords
    else coords :+ coords.head

  private[geometry] lazy val jtsPolygon: JTSPolygon = {
    val gf = new GeometryFactory()
    val points = closed.map(c => new geom.Coordinate(c.x, c.y)).asJava
    gf.createPolygon(new LinearRing(new CoordinateArraySequence(points.toArray(new Array[Coordinate](points.size))), gf), null)
  }

  def transformCRS[NEW_CRS: CRSType]: Polygon[NEW_CRS] = {
    val transform = findTransformTo[NEW_CRS]
    val convertedCoords = coords.map(c => c.transformWith[NEW_CRS](transform))
    Polygon[NEW_CRS](convertedCoords)
  }

  def contains(coord: Coord2D[CRS]): Boolean = {
    coord.jtsPoint.within(jtsPolygon)
  }

  private def area: Double =
    jtsPolygon.getArea
}

object Polygon {
  def apply[CRS: CRSType](coords: (Double, Double)*): Polygon[CRS] = {
    val gCoords = coords.toVector.map { case (x, y) => Coord2D[CRS](x, y) }
    new Polygon[CRS](gCoords)
  }

  def area[CRS: CRSType](poly: Polygon[CRS]): Double = {
    poly.area
  }

  def areaSquareMeters[CRS: MeterCRSType](poly: Polygon[CRS]): Double = {
    poly.area
  }
}
