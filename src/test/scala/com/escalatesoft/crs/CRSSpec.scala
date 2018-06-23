package com.escalatesoft.crs

import com.escalatesoft.crs.CRSType.CRSDefinitions.{EPSG_32615, EPSG_4326}
import com.escalatesoft.geometry.Coord2D
import org.scalatest.{FunSpec, Matchers}

class CRSSpec extends FunSpec with Matchers {
  describe ("Existential CRS lookup") {
    val anyOldCRSID = "EPSG:32615"

    val someCRS = CRSType.optCrsFromId(anyOldCRSID).get
    import someCRS.SOME_CRS

    val someCRSCoord = Coord2D[SOME_CRS](10.0, 10.0)
    val utmZone15NCoord = someCRSCoord.transformCRS[EPSG_32615]

    utmZone15NCoord.x should be (10.0 +- 1e-9)
    utmZone15NCoord.y should be (10.0 +- 1e-9)

  }

}
