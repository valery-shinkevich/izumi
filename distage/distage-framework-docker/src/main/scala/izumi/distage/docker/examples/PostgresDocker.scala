package izumi.distage.docker.examples

import izumi.distage.docker.ContainerDef
import izumi.distage.docker.Docker.{ContainerConfig, DockerPort}
import izumi.distage.model.definition.ModuleDef
import izumi.fundamentals.reflection.Tags.TagK

object PostgresDocker extends ContainerDef {
  val primaryPort: DockerPort = DockerPort.TCP(5432)

  override def config: Config = {
    ContainerConfig(
      image = "library/postgres:latest",
      ports = Seq(primaryPort),
      reuse = true,
    )
  }
}

class PostgresDockerModule[F[_]: TagK] extends ModuleDef {
  make[PostgresDocker.Container].fromResource {
    PostgresDocker.make[F]
  }
}
