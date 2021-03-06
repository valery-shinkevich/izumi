package izumi.distage.model.plan

import izumi.distage.model.reflection.universe.RuntimeDIUniverse._

sealed trait GCMode {
  def toSet: Set[DIKey]
}

object GCMode {
  def apply(root: DIKey, roots: DIKey*): GCMode = {
    GCRoots(roots.toSet + root)
  }
  def apply(roots: Set[DIKey]): GCMode = {
    GCMode.GCRoots(roots)
  }

  @deprecated("Use GCMode.apply(set)", "will be removed in 0.10.2")
  def fromSet(roots: Set[DIKey]): GCMode = {
    if (roots.nonEmpty) GCRoots(roots) else NoGC
  }

  final case class GCRoots(roots: Set[DIKey]) extends GCMode {
    assert(roots.nonEmpty, "GC roots set cannot be empty")

    override def toSet: Set[DIKey] = roots
  }

  case object NoGC extends GCMode {
    override def toSet: Set[DIKey] = Set.empty
  }

}
