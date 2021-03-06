package izumi.distage.testkit.docker

import distage.SafeType
import izumi.distage.docker.DockerContainer
import izumi.distage.docker.examples.PostgresDocker
import izumi.distage.model.definition.DIResource
import izumi.fundamentals.platform.functional.Identity
import org.scalatest.wordspec.AnyWordSpec

final class DockerContainerProviderTest extends AnyWordSpec {
  "Return type is correct" in {
    assert(PostgresDocker.make[Identity].get.ret == SafeType.get[DIResource[Identity, DockerContainer[PostgresDocker.Tag]]])
  }
}
