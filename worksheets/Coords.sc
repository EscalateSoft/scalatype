import com.escalatesoft.crs.CRSType.CRSDefinitions._
import com.escalatesoft.geometry.Coord2D

val coordsWGS84: Coord2D[EPSG_4326] = Coord2D[EPSG_4326](-121.6544, 37.1305)

def showMap(center: Coord2D[EPSG_4326]): Unit = {
  println(s"Showing map centered on $center")
}

showMap(coordsWGS84)

val coordsUTM10: Coord2D[EPSG_32610] = coordsWGS84.transformCRS[EPSG_32610]

//showMap(coordsUTM10)


showMap(coordsUTM10.transformCRS[EPSG_4326])


