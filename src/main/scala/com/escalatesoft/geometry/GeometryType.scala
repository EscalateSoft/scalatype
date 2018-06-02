package com.escalatesoft.geometry

import com.escalatesoft.crs.CRSDef
import org.geotools.referencing.{CRS => GCRS}
import org.opengis.referencing.crs.CoordinateReferenceSystem

abstract class GeometryType[CRS: CRSDef] {
  lazy val crs: CoordinateReferenceSystem = implicitly[CRSDef[CRS]].crs

  protected def findTransformTo[NEW_CRS: CRSDef] = {
    val newCRS = implicitly[CRSDef[NEW_CRS]]
    val transform = GCRS.findMathTransform(crs, newCRS.crs, true)
    transform
  }
}
