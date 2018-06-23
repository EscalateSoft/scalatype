package com.escalatesoft.geometry

import com.escalatesoft.crs.CRSType
import com.escalatesoft.tmap.TMap
import scala.reflect.runtime.universe.TypeTag

case class Feature[CRS: CRSType, GEOM <: GeometryType[CRS], +T](geometry: GEOM, attributes: T) {
  def mapAttributes[U](transform: T => U): Feature[CRS, GEOM, U] = {
    Feature[CRS, GEOM, U](geometry, transform(attributes))
  }

  private def addAttrInner[TMT, TM <: TMap[TMT], ATTR: TypeTag](attr: ATTR)(
    implicit ev: T <:< TM): Feature[CRS, GEOM, TMap[TMT with ATTR]] = {

    val asTmap: TMap[TMT] = attributes

    val newTMap = asTmap + attr
    Feature[CRS, GEOM, TMap[TMT with ATTR]](geometry, newTMap)
  }
}

object Feature {
  implicit class RichFeature[CRS: CRSType, GEOM <: GeometryType[CRS], T](
      feature: Feature[CRS, GEOM, TMap[T]]) {

    def addAttr[ATTR: TypeTag](attr: ATTR): Feature[CRS, GEOM, TMap[T with ATTR]] =
      feature.addAttrInner[T, TMap[T], ATTR](attr)
  }
}
