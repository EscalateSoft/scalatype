import com.escalatesoft.crs.CRSType.CRSDefinitions.{EPSG_32615, EPSG_32616}
import com.escalatesoft.geometry.Polygon
import com.escalatesoft.raster.{GridCoverage, GridCoverageWithCRS}

val cov32615 = new GridCoverage[EPSG_32615]

val covWithSomeCRS = GridCoverageWithCRS(cov32615)

val polyIn32616 = Polygon[EPSG_32616](
  (0.0, 0.0), (0.0, 10.0), (10.0, 10.0), (10.0, 0.0), (0.0, 0.0)
)

covWithSomeCRS.cropToAnyPolygon(polyIn32616)

// covWithSomeCRS.coverage.cropToPolygon(polyIn32616)

import covWithSomeCRS.COV_CRS

covWithSomeCRS.coverage.cropToPolygon(polyIn32616.transformCRS[COV_CRS])


