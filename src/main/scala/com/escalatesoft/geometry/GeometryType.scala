package com.escalatesoft.geometry

import com.escalatesoft.crs.CRSType
import org.geotools.referencing.{CRS => GCRS}
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.opengis.referencing.operation.MathTransform

abstract class GeometryType[CRS: CRSType] {
  lazy val crs: CoordinateReferenceSystem = implicitly[CRSType[CRS]].crs

  protected def findTransformTo[NEW_CRS: CRSType]: MathTransform = {
    val newCRS = implicitly[CRSType[NEW_CRS]]
    val transform = GCRS.findMathTransform(crs, newCRS.crs, false)
    transform
  }
}
