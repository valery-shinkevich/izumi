package izumi.distage.model.reflection.universe

import izumi.fundamentals.reflection.macrortti.{LightTypeTag, LightTypeTagImpl}
import izumi.fundamentals.reflection.{SafeType0, WithTags}

trait WithDISafeType {
  this: DIUniverseBase with WithTags =>

  // TODO: hotspot, hashcode on keys is inefficient
  case class SafeType protected (override val tpe: TypeNative, override protected[izumi] val fullLightTypeTag: LightTypeTag) extends SafeType0[u.type](u, tpe, fullLightTypeTag)

  object SafeType {
    // FIXME TODO constructing SafeType from a runtime type tag
    @deprecated("constructing SafeType from a runtime type tag", "0.9.0")
    def apply(tpe: TypeNative): SafeType = new SafeType(tpe, LightTypeTagImpl.makeFLTT(u)(tpe))

    def get[T: Tag]: SafeType = SafeType(Tag[T].tag.tpe, Tag[T].fullLightTypeTag)
    def getK[K[_]: TagK]: SafeType = SafeType(TagK[K].tag.tpe, TagK[K].fullLightTypeTag)

    def unsafeGetWeak[T](implicit weakTag: WeakTag[T]): SafeType = SafeType(weakTag.tag.tpe, weakTag.fullLightTypeTag)

    implicit class SafeTypeUnsafeToTag(tpe: SafeType) {
      def unsafeToTag[T]: Tag[T] = Tag.unsafeFromSafeType[T](tpe)
    }
  }

}
