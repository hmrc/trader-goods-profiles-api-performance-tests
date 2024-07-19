/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.perftests.TGP

import io.gatling.http.HeaderNames.Authorization
import org.apache.pekko.actor.ActorSystem
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object TGPAuthRequests extends ServicesConfiguration {
  private implicit val system: ActorSystem = ActorSystem()
  val wsClient: StandaloneAhcWSClient      = StandaloneAhcWSClient()

  private val authApiBaseUrl: String = baseUrlFor("auth-login-api")
  private val authApiUrl: String     = s"$authApiBaseUrl/government-gateway/session/login"

  def getAuthToken(eori: String): String = {
    val response = Await.result(
      wsClient
        .url(authApiUrl)
        .post(
          Json.obj(
            "affinityGroup"      -> "Organisation",
            "credentialStrength" -> "strong",
            "confidenceLevel"    -> 50,
            "credentialRole"     -> "Admin",
            "credId"             -> UUID.randomUUID().toString,
            "enrolments"         -> Seq(
              Json.obj(
                "key"         -> "HMRC-CUS-ORG",
                "identifiers" -> Seq(
                  Json.obj(
                    "key"   -> "EORINumber",
                    "value" -> eori
                  )
                ),
                "state"       -> "Activated"
              )
            )
          )
        ),
      5.seconds
    )
    require(response.status == 201, "Unable to create auth token")

    response.headers
      .getOrElse(Authorization.toString, Seq("unable to get auth header"))
      .flatMap(_.split(","))
      .find(_.startsWith("Bearer"))
      .get

  }

}
