package izumi

import org.scalatest.WordSpec

import scala.annotation.{StaticAnnotation, TypeConstraint}
import scala.language.implicitConversions

class TypeConstraintExperiment extends WordSpec {

  // Making DIAnnotation inherit type constraint could maybe allow
  // extraction without using a macro...
  // Still it wouldn't work for non-type annotations or for java @Named...
  class Id(val name: String) extends StaticAnnotation with TypeConstraint

  class PrintType[T](override val toString: String)
  object PrintType {
    def apply[A: PrintType]: PrintType[A] = implicitly

    import scala.reflect.runtime.universe._
    implicit def materialize[T: WeakTypeTag]: PrintType[T] = {
      val tpe = weakTypeOf[T]
      val str = (tpe.widen :: tpe.baseClasses.map(symbol => tpe.baseType(symbol))).toString
      new PrintType[T](str)
    }
  }

  class A0
  class B0
  class C0(a: A0, b: B0)

  def xa(a: A0@Id("a-id"), b: B0@Id("b-id")) = {
    new C0(a, b): @Id("c-id")
  }

  case class Extract[T](x: PrintType[_]) { override def toString = x.toString }
  object Extract {
    implicit def fromTpe[X: PrintType]: Extract[X] = Extract(PrintType[X])
    implicit def convertTpe[X: PrintType](x: X): Extract[X] = Extract(PrintType[X])
  }

  case class GetReturn[F, R](x: PrintType[_]) { override def toString = x.toString }
  object GetReturn {
    implicit def fromTpe[X <: (A, B) => R: PrintType, A, B, R]: GetReturn[X with ((A, B) => R), R] = GetReturn(PrintType[X])
  }

  def tt[A, B, C](f: (A, B) => C)(implicit e: Extract[f.type]) = {
    println(e)
  }

  def mag[R](e: Extract[R]) = {
    println(e)
  }

  def tc[R](r: AnyRef)(implicit e: GetReturn[r.type, R]) = {
    println(e.x)
  }

  "fghfgdh" in {
    tt(xa)
    mag(xa _)
    tc(xa _)
  }

}
