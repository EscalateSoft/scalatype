import com.escalatesoft.crs.CRSDef.CRSDefinitions.longitudeFirst
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.geotools.referencing.CRS

val epsg4326 = CRS.decode("EPSG:4326", longitudeFirst)
val epsg32615 = CRS.decode("EPSG:32615")
val epsg32616 = CRS.decode("EPSG:32616")


def ensureSameCRS(crs1: CoordinateReferenceSystem,
  crs2: CoordinateReferenceSystem) =
  if (crs1.getCoordinateSystem != crs2.getCoordinateSystem)
    throw new IllegalStateException("Incompatible CRSs")

ensureSameCRS(epsg4326, epsg4326)
ensureSameCRS(epsg4326, epsg32615)

