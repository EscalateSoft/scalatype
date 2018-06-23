package com.escalatesoft.raster

import com.escalatesoft.crs.CRSType

object ResampleCRS {
  implicit class ResampleCRS[CRS: CRSType](gc: GridCoverage[CRS]) {

    def resampleCRS[NEW_CRS]: GridCoverage[NEW_CRS] = ???

    def resampleCRS[NEW_CRS](
      resolution: Double, interpolationType: InterpolationType
    ): GridCoverage[NEW_CRS] = ???
  }
}
