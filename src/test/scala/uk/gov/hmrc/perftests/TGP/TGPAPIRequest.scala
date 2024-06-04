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
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object TGPAPIRequest extends ServicesConfiguration {

  val authLoginStub: String = "/auth-login-stub"
  val bearerToken: String   = readProperty("bearerToken", "${accessToken}")
  val baseUrl: String       =
    if (!runLocal) s"${baseUrlFor("tgp-api")}/customs/traders/goods-profiles"
    else baseUrlFor("tgp-api")

  def generateHeaders(
    contentType: String = "application/json",
    acceptHeader: String = "application/vnd.hmrc.1.0+json"
  ): Map[CharSequence, String] =
    if (!runLocal) {
      Map(
        HttpHeaderNames.ContentType   -> contentType,
        HttpHeaderNames.Authorization -> s"Bearer $bearerToken",
        HttpHeaderNames.Accept        -> acceptHeader,
      "X-Client-ID"                 -> "test"
      )
    } else
      Map(
        HttpHeaderNames.ContentType   -> contentType,
        HttpHeaderNames.Authorization -> bearerToken,
        HttpHeaderNames.Accept        -> acceptHeader,
        "X-Client-ID"                 -> "test"
      )

  val tgpapiGetSuccess200: HttpRequestBuilder =
    http("GET TGP Api Record Success Response 200")
      .get(s"$baseUrl/GB123456789001/records/8ebb6b04-6ab0-4fe2-ad62-e6389a8a204f")
      .headers(generateHeaders())
      .check(status.is(200))
}
