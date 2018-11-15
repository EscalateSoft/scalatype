package com.escalatesoft.raster

import com.escalatesoft.crs.CRSType
import com.escalatesoft.geometry.Polygon

class GridCoverage[CRS: CRSType] {
  def cropToPolygon(poly: Polygon[CRS]): GridCoverage[CRS] = this  // would do a real crop
}

class InterpolationType

abstract class GridCoverageWithCRS {
  type COV_CRS
  implicit val COV_CRS: CRSType[COV_CRS]
  val coverage: GridCoverage[COV_CRS]

  def cropToAnyPolygon[POLY_CRS: CRSType](poly: Polygon[POLY_CRS]): GridCoverageWithCRS = {
    GridCoverageWithCRS(coverage.cropToPolygon(poly.transformCRS[COV_CRS]))
  }
}

object GridCoverageWithCRS {
  def apply[CRS: CRSType](cov: GridCoverage[CRS]): GridCoverageWithCRS = {
    val crsType = implicitly[CRSType[CRS]]
    new GridCoverageWithCRS {
      type COV_CRS = CRS
      implicit val COV_CRS: CRSType[COV_CRS] = crsType
      val coverage: GridCoverage[COV_CRS] = cov
    }
  }
}