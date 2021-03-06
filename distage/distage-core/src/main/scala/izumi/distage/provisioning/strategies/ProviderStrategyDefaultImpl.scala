package izumi.distage.provisioning.strategies

import izumi.distage.model.exceptions.InvalidPlanException
import izumi.distage.model.plan.ExecutableOp.WiringOp
import izumi.distage.model.provisioning.strategies.ProviderStrategy
import izumi.distage.model.provisioning.{NewObjectOp, ProvisioningKeyProvider, WiringExecutor}
import izumi.distage.model.reflection.universe.RuntimeDIUniverse
import izumi.fundamentals.platform.language.unused

class ProviderStrategyDefaultImpl extends ProviderStrategy  {
  def callProvider(context: ProvisioningKeyProvider, @unused executor: WiringExecutor, op: WiringOp.CallProvider): Seq[NewObjectOp.NewInstance] = {

    val args: Seq[RuntimeDIUniverse.TypedRef[_]] = op.wiring.associations.map {
      param =>
        context.fetchKey(param.key, param.isByName) match {
          case Some(dep) =>
            RuntimeDIUniverse.TypedRef(dep, param.key.tpe, param.isByName)
          case _ =>
            throw new InvalidPlanException("The impossible happened! Tried to instantiate class," +
                s" but the dependency has not been initialized: Class: $op.target, dependency: $param")
        }
    }

    val instance = op.wiring.provider.unsafeApply(args)
    Seq(NewObjectOp.NewInstance(op.target, instance))
  }
}

