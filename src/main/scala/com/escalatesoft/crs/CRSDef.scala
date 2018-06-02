package com.escalatesoft.crs

import java.io.File

import org.geotools.coverage.grid.GridCoverage2D
import org.geotools.gce.geotiff.GeoTiffReader
import org.geotools.referencing.CRS
import org.opengis.referencing.crs.CoordinateReferenceSystem

abstract class CRSDef[T](val crs: CoordinateReferenceSystem) {
  def crsId: String = crs.getIdentifiers.toArray.head.toString
}

abstract class AngleCRSDef[T](override val crs: CoordinateReferenceSystem) extends CRSDef[T](crs)

abstract class MeterCRSDef[T](override val crs: CoordinateReferenceSystem) extends CRSDef[T](crs)

object CRSDef {

  trait SomeCRS extends Serializable {
    type SOME_CRS
    implicit val SOME_CRS: CRSDef[SOME_CRS]

    override def toString: String = s"SomeCRS($SOME_CRS)"

    override def equals(obj: Any): Boolean = {
      obj match {
        case otherCRS: SomeCRS =>
          this.SOME_CRS == otherCRS.SOME_CRS
        case _ => false
      }
    }

    override def hashCode(): Int = SOME_CRS.##
  }

  trait SomeMeterCRS extends SomeCRS {
    override implicit val SOME_CRS: MeterCRSDef[SOME_CRS]
  }

  object CRSDefinitions {
    // After this point, all decoded CRSs will be X first, strictly
    System.setProperty("org.geotools.referencing.forceXY", "true")
    val longitudeFirst = true // use this to swap order of coords

    // general WGS84
    val EPSG_4326_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:4326", longitudeFirst)
    type EPSG_4326 = EPSG_4326_CRS.type
    implicit object EPSG_4326 extends AngleCRSDef[EPSG_4326](EPSG_4326_CRS)

    // NAD83
    val EPSG_4269_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:4269", longitudeFirst)
    type EPSG_4269 = EPSG_4269_CRS.type
    implicit object EPSG_4269 extends AngleCRSDef[EPSG_4269](EPSG_4269_CRS)

    // WGS84 UTM zone 10N
    val EPSG_32610_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:32610", longitudeFirst)
    type EPSG_32610 = EPSG_32610_CRS.type
    implicit object EPSG_32610 extends MeterCRSDef[EPSG_32610](EPSG_32610_CRS)

    // WGS84 UTM zone 11N
    val EPSG_32611_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:32611", longitudeFirst)
    type EPSG_32611 = EPSG_32611_CRS.type
    implicit object EPSG_32611 extends MeterCRSDef[EPSG_32611](EPSG_32611_CRS)

    // WGS84 UTM zone 12N
    val EPSG_32612_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:32612", longitudeFirst)
    type EPSG_32612 = EPSG_32612_CRS.type
    implicit object EPSG_32612 extends MeterCRSDef[EPSG_32612](EPSG_32612_CRS)

    // WGS84 UTM zone 13N
    val EPSG_32613_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:32613", longitudeFirst)
    type EPSG_32613 = EPSG_32613_CRS.type
    implicit object EPSG_32613 extends MeterCRSDef[EPSG_32613](EPSG_32613_CRS)

    // WGS84 UTM zone 14N
    val EPSG_32614_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:32614", longitudeFirst)
    type EPSG_32614 = EPSG_32614_CRS.type
    implicit object EPSG_32614 extends MeterCRSDef[EPSG_32614](EPSG_32614_CRS)

    // WGS84 UTM zone 15N
    val EPSG_32615_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:32615", longitudeFirst)
    type EPSG_32615 = EPSG_32615_CRS.type
    implicit object EPSG_32615 extends MeterCRSDef[EPSG_32615](EPSG_32615_CRS)

    // WGS84 UTM zone 16N
    val EPSG_32616_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:32616", longitudeFirst)
    type EPSG_32616 = EPSG_32616_CRS.type
    implicit object EPSG_32616 extends MeterCRSDef[EPSG_32616](EPSG_32616_CRS)

    // WGS84 UTM zone 17N
    val EPSG_32617_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:32617", longitudeFirst)
    type EPSG_32617 = EPSG_32617_CRS.type
    implicit object EPSG_32617 extends MeterCRSDef[EPSG_32617](EPSG_32617_CRS)

    // WGS84 UTM zone 18N
    val EPSG_32618_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:32618", longitudeFirst)
    type EPSG_32618 = EPSG_32618_CRS.type
    implicit object EPSG_32618 extends MeterCRSDef[EPSG_32618](EPSG_32618_CRS)

    // WGS84 UTM zone 19N
    val EPSG_32619_CRS: CoordinateReferenceSystem = CRS.decode("EPSG:32619", longitudeFirst)
    type EPSG_32619 = EPSG_32619_CRS.type
    implicit object EPSG_32619 extends MeterCRSDef[EPSG_32619](EPSG_32619_CRS)

    private def crsTuple[A](crs: CRSDef[A]): (String, CRSDef[A]) =
      (crs.crsId, crs)

    @volatile lazy val _registered: Map[String, CRSDef[_]] =
      Seq(
        // NAD83
        EPSG_4269,
        // WGS 84 global
        EPSG_4326,
        // Contiguous US UTM zones
        EPSG_32610,
        EPSG_32611,
        EPSG_32612,
        EPSG_32613,
        EPSG_32614,
        EPSG_32615,
        EPSG_32616,
        EPSG_32617,
        EPSG_32618,
        EPSG_32619
      ).map(crsTuple(_)).toMap

    def apply(): Seq[String] = _registered.keys.toVector

  }

  def optCrsFromId(id: String): Option[SomeCRS] = {
    for (ccrs <- CRSDefinitions._registered.get(id)) yield {
      new SomeCRS {
        type SOME_CRS = ccrs.crs.type
        val SOME_CRS: CRSDef[ccrs.crs.type] =
          ccrs.asInstanceOf[CRSDef[SOME_CRS]]
      }
    }
  }

  def optMeterCrsFromId(id: String): Option[SomeMeterCRS] = {
    (for (ccrs <- CRSDefinitions._registered.get(id)) yield {
      ccrs match {
        case meterBased: MeterCRSDef[_] =>
          Some(new SomeMeterCRS {
            override type SOME_CRS = meterBased.crs.type
            override implicit val SOME_CRS: MeterCRSDef[meterBased.crs.type] =
              meterBased.asInstanceOf[MeterCRSDef[SOME_CRS]]
          })
        case _ => None
      }
    }).flatten
  }

  def crsFromRuntime(ccrs: CRSDef[_]): SomeCRS = {
    new SomeCRS {
      type SOME_CRS = ccrs.crs.type
      val SOME_CRS: CRSDef[ccrs.crs.type] = ccrs.asInstanceOf[CRSDef[SOME_CRS]]
    }
  }

  def crsFromTiff(tiffFile: File): SomeCRS = {
    val reader: GeoTiffReader = new GeoTiffReader(tiffFile)
    val gc2d: GridCoverage2D = reader.read(null)

    val coordref: String =
      gc2d.getCoordinateReferenceSystem2D.getIdentifiers.toArray.head.toString
    CRSDef.optCrsFromId(coordref).getOrElse(throw new IllegalStateException(s"Unregistered CRS: $coordref"))
  }

}
