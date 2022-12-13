//> using plugin "org.typelevel:::kind-projector:0.13.2"
//> using lib "com.disneystreaming.smithy4s::smithy4s-core:0.17.1"
//> using scala "2.13.10"
//> using publish.repository "central-s01"
//> using publish.organization "org.polyvariant"
//> using publish.name "smithy4s-utils"
package smithy4sutils.cache

import smithy4s.kinds._

object EndpointFunction {
  def cached[
      Op[_, _, _, _, _],
      G[_, _, _, _, _]
  ](
      function: PolyFunction5[smithy4s.Endpoint[Op, *, *, *, *, *], G],
      allEndpoints: List[
        Kind5.Existential[smithy4s.Endpoint[Op, *, *, *, *, *]]
      ]
  ): PolyFunction5[smithy4s.Endpoint[Op, *, *, *, *, *], G] =
    new PolyFunction5[smithy4s.Endpoint[Op, *, *, *, *, *], G] {

      private val map: Map[String, Any] = {
        val builder = Map.newBuilder[String, Any]
        allEndpoints.foreach(input =>
          builder += input.name -> function
            .apply(
              input
                .asInstanceOf[smithy4s.Endpoint[Op, Any, Any, Any, Any, Any]]
            )
            .asInstanceOf[Any]
        )
        builder.result()
      }

      def apply[A0, A1, A2, A3, A4](
          input: smithy4s.Endpoint[Op, A0, A1, A2, A3, A4]
      ): G[A0, A1, A2, A3, A4] =
        map(Kind5.existential(input).name).asInstanceOf[G[A0, A1, A2, A3, A4]]

    }

}
