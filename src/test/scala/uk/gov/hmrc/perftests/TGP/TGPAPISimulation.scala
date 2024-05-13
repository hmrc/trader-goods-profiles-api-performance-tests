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

import io.gatling.core.Predef._

import io.gatling.http.request.builder.HttpRequestBuilder

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner

import uk.gov.hmrc.perftests.TGP.TGPAPIRequest._
import uk.gov.hmrc.perftests.TGP.TGPAuthRequests.{authLogin, getAccessTokenGG, getAuthId, getCredentialsPage, getGrantAuthority200, getGrantAuthority303, getSession, getStart, grantAuthorityRedirect, grantAuthorityRedirect2, postAuthLogin, submitGrantAuthority, taxIdentifier}
import java.util.concurrent.atomic.AtomicInteger
import scala.util.Random

class TGPAPISimulation extends PerformanceTestRunner {

  val RNG             = new Random
  private val Counter = new AtomicInteger(0)

  def setupSession(nextId: Int, lrn: String, session: Session): Session =
    session.setAll(
      List(
        "currentTime"    -> System.currentTimeMillis().toString,
        "random"         -> Math.abs(RNG.nextInt()),
        "nextId"         -> nextId,
        "localRefNumber" -> lrn,
        "itemNumber"     -> 1
      )
    )

  def authRequests(identifier: String): Seq[HttpRequestBuilder] =
    if (!runLocal) {
      Seq(
        getAuthId,
        getStart,
        getGrantAuthority303,
        getCredentialsPage,
        authLogin(identifier),
        grantAuthorityRedirect,
        grantAuthorityRedirect2,
        getGrantAuthority200,
        submitGrantAuthority,
        getAccessTokenGG
      )
    } else {
      Seq(postAuthLogin(identifier), getSession)
    }

  setup("auth-part", "Create an access token ").withRequests(authRequests(taxIdentifier): _*)

  setup("tgp-api-get-success-200-part", "GET TGP Api Record Request should return 200 Created status code")
    .withRequests(tgpapiGetSuccess200)

  runSimulation()
}
