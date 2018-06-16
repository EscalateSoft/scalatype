package com.escalatesoft.geometry

import com.escalatesoft.crs.CRSType
import com.vividsolutions.jts.geom.{Coordinate, GeometryFactory, Point => JTSPoint}
import org.geotools.geometry.jts.JTS
import org.opengis.referencing.operation.MathTransform

case class Coord2D[CRS: CRSType](x: Double, y: Double) extends GeometryType[CRS] {

  private lazy val jtsCoordinate: Coordinate =
    new Coordinate(x, y)

  def transformCRS[NEW_CRS: CRSType]: Coord2D[NEW_CRS] = {
    val transform: MathTransform = findTransformTo[NEW_CRS]
    transformWith[NEW_CRS](transform)
  }

  private[geometry] lazy val jtsPoint: JTSPoint = {
    val gf = new GeometryFactory()
    gf.createPoint(jtsCoordinate)
  }

  private[geometry] def transformWith[NEW_CRS: CRSType](transform: MathTransform): Coord2D[NEW_CRS] = {
    val newCoord = if (transform.isIdentity) jtsCoordinate else
      JTS.transform(new Coordinate(x, y), new Coordinate(), transform)

    Coord2D[NEW_CRS](newCoord.x, newCoord.y)
  }

  def within(poly: Polygon[CRS]): Boolean = poly.contains(this)
}

