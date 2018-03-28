package com.github.pshirshov.izumi.idealingua.model.il

import com.github.pshirshov.izumi.idealingua.model.common.TypeId.ServiceId
import com.github.pshirshov.izumi.idealingua.model.common._
import com.github.pshirshov.izumi.idealingua.model.il.ILAst.Service.DefMethod._
import com.github.pshirshov.izumi.idealingua.model.il.ILAst._

import scala.util.hashing.MurmurHash3

class TypeSignature(typespace: Typespace) {
  def signature(id: TypeId): Int = {
    signature(id, Set.empty)
  }

  def signature(id: ServiceId): Int = {
    serviceSignature(id, Set.empty)
  }

  protected def serviceSignature(id: ServiceId, seen: Set[ServiceId]): Int = {
    val service = typespace(id)
    MurmurHash3.orderedHash(simpleSignature(service.id) +: service.methods.flatMap {
      case r: RPCMethod =>
        Seq(MurmurHash3.stringHash(r.name), MurmurHash3.orderedHash(r.signature.asList.map(signature)))
    })
  }

  protected def signature(id: TypeId, seen: Set[TypeId]): Int = {
    id match {
      case b: Primitive =>
        simpleSignature(b)

      case b: Generic =>
        val argSig = b.args.map {
          case v if seen.contains(v) => simpleSignature(v)
          case argid => signature(argid, seen + argid)
        }
        MurmurHash3.orderedHash(simpleSignature(b) +: argSig)


      case _ =>
        val primitiveSignature = explode(typespace.apply(id))
        val numbers = primitiveSignature.flatMap {
          tf =>
            Seq(simpleSignature(tf.typeId), signature(tf.typeId, seen))
        }
        MurmurHash3.orderedHash(numbers)
    }
  }

  protected def simpleSignature(id: TypeId): Int = {
    MurmurHash3.orderedHash((id.pkg :+ id.name).map(MurmurHash3.stringHash))
  }

  protected def simpleSignature(id: ServiceId): Int = {
    MurmurHash3.orderedHash((id.pkg :+ id.name).map(MurmurHash3.stringHash))
  }

  protected def explode(defn: ILAst): List[TrivialField] = {
    defn match {
      case t: Interface =>
        t.interfaces.flatMap(i => explode(typespace(i))) ++
        t.concepts.flatMap(i => explode(typespace(i))) ++
          t.fields.flatMap(explode)

      case t: Adt =>
        t.alternatives.map(typespace.apply).flatMap(explode)

      case t: DTO =>
        t.interfaces.flatMap(i => explode(typespace(i)))

      case t: Identifier =>
        t.fields.flatMap(explode)

      case _: Alias =>
        List()

      case _: Enumeration =>
        List()
    }
  }

  protected def explode(defn: Field): List[TrivialField] = {
    defn.typeId match {
      case t: Builtin =>
        List(TrivialField(t, defn.name))
      case t =>
        explode(typespace.apply(t))
    }
  }

}
