package com.escalatesoft.crs

import java.io.File

import com.escalatesoft.crs.CRSType.CRSDefinitions.longitudeFirst
import org.geotools.coverage.grid.GridCoverage2D
import org.geotools.gce.geotiff.GeoTiffReader
import org.geotools.referencing.CRS
import org.opengis.referencing.crs.CoordinateReferenceSystem

abstract class CRSDef(val crsCode: String) {
  val crs: CoordinateReferenceSystem = CRS.decode(crsCode, longitudeFirst)
}

abstract class CRSType[T] {
  type CRS_TYPE = T
  def apply(): CRSDef
  val crsDef: CRSDef = apply()
  val crs: CoordinateReferenceSystem = crsDef.crs
  val crsId: String = crsDef.crs.getIdentifiers.toArray.head.toString
  implicit val implicitCrsDef: CRSType[CRS_TYPE] = this
}

abstract class AngleCRSType[T] extends CRSType[T] {
  override implicit val implicitCrsDef: AngleCRSType[CRS_TYPE] = this
}

abstract class MeterCRSType[T] extends CRSType[T] {
  override implicit val implicitCrsDef: MeterCRSType[CRS_TYPE] = this
}

object CRSType {

  trait SomeCRS {
    type SOME_CRS
    implicit val SOME_CRS: CRSType[SOME_CRS]

    override def toString: String = s"SomeCRS($SOME_CRS)"
    override def equals(obj: Any): Boolean = {
      obj match {
        case otherCRS: SomeCRS =>
          this.SOME_CRS.crs == otherCRS.SOME_CRS.crs
        case _ => false
      }
    }
    override def hashCode(): Int = {
      SOME_CRS.crs.##
    }
  }

  trait SomeMeterCRS extends SomeCRS {
    override implicit val SOME_CRS: MeterCRSType[SOME_CRS]
  }

  object CRSDefinitions {
    // After this point, all decoded CRSs will be X first, strictly
    System.setProperty("org.geotools.referencing.forceXY", "true")
    val longitudeFirst = true // use this to swap order of coords

    // general WGS84
    case class EPSG_4326() extends CRSDef("EPSG:4326")
    object EPSG_4326 extends AngleCRSType[EPSG_4326]

    // WGS84 UTM zone 10N
    case class EPSG_32610() extends CRSDef("EPSG:32610")
    object EPSG_32610 extends MeterCRSType[EPSG_32610]

    // WGS84 UTM zone 11N
    case class EPSG_32611() extends CRSDef("EPSG:32611")
    object EPSG_32611 extends MeterCRSType[EPSG_32611]

    // WGS84 UTM zone 12N
    case class EPSG_32612() extends CRSDef("EPSG:32612")
    object EPSG_32612 extends MeterCRSType[EPSG_32612]

    // WGS84 UTM zone 13N
    case class EPSG_32613() extends CRSDef("EPSG:32613")
    object EPSG_32613 extends MeterCRSType[EPSG_32613]

    // WGS84 UTM zone 14N
    case class EPSG_32614() extends CRSDef("EPSG:32614")
    object EPSG_32614 extends MeterCRSType[EPSG_32614]

    // WGS84 UTM zone 15N
    case class EPSG_32615() extends CRSDef("EPSG:32615")
    object EPSG_32615 extends MeterCRSType[EPSG_32615]

    // WGS84 UTM zone 16N
    case class EPSG_32616() extends CRSDef("EPSG:32616")
    object EPSG_32616 extends MeterCRSType[EPSG_32616]

    // WGS84 UTM zone 17N
    case class EPSG_32617() extends CRSDef("EPSG:32617")
    object EPSG_32617 extends MeterCRSType[EPSG_32617]

    // WGS84 UTM zone 18N
    case class EPSG_32618() extends CRSDef("EPSG:32618")
    object EPSG_32618 extends MeterCRSType[EPSG_32618]

    // WGS84 UTM zone 19N
    case class EPSG_32619() extends CRSDef("EPSG:32619")
    object EPSG_32619 extends MeterCRSType[EPSG_32619]

    // NAD83
    case class EPSG_4269 private() extends CRSDef("EPSG:4269")
    object EPSG_4269 extends AngleCRSType[EPSG_4269]

    object DisplayOnly {
      case class EPSG_3857() extends CRSDef("EPSG:3857")
      object EPSG_3857 extends CRSType[EPSG_3857]
    }


    private def crsTuple(crs: CRSType[_]): (String, CRSType[_]) =
      (crs.crsId, crs)

    lazy val _registered: Map[String, CRSType[_]] =
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
      ).map(c => crsTuple(c)).toMap

    def apply(): Seq[String] = _registered.keys.toVector

  }

  def optCrsFromId(id: String): Option[SomeCRS] = {
    for (ccrs <- CRSDefinitions._registered.get(id)) yield {
      new SomeCRS {
        type SOME_CRS = ccrs.CRS_TYPE
        val SOME_CRS: CRSType[ccrs.CRS_TYPE] =
          ccrs.asInstanceOf[CRSType[SOME_CRS]]
      }
    }
  }

  def optMeterCrsFromId(id: String): Option[SomeMeterCRS] = {
    (for (ccrs <- CRSDefinitions._registered.get(id)) yield {
      ccrs match {
        case meterBased: MeterCRSType[_] =>
          Some(new SomeMeterCRS {
            override type SOME_CRS = meterBased.CRS_TYPE
            override implicit val SOME_CRS: MeterCRSType[meterBased.CRS_TYPE] =
              meterBased.asInstanceOf[MeterCRSType[SOME_CRS]]
          })
        case _ => None
      }
    }).flatten
  }

  def crsFromRuntime(ccrs: CRSType[_]): SomeCRS = {
    new SomeCRS {
      type SOME_CRS = ccrs.CRS_TYPE
      val SOME_CRS: CRSType[ccrs.CRS_TYPE] = ccrs.asInstanceOf[CRSType[SOME_CRS]]
    }
  }

  def crsFromTiff(tiffFile: File): SomeCRS = {
    val reader: GeoTiffReader = new GeoTiffReader(tiffFile)
    val gc2d: GridCoverage2D = reader.read(null)

    val coordref: String =
      gc2d.getCoordinateReferenceSystem2D.getIdentifiers.toArray.head.toString
    CRSType.optCrsFromId(coordref).getOrElse(throw new IllegalStateException(s"Unregistered CRS: $coordref"))
  }

}
