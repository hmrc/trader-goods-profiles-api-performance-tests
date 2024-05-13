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
    if (!runLocal) s"${baseUrlFor("tgp-api")}"
    else baseUrlFor("tgp-api")

  def generateHeaders(
    contentType: String = HttpHeaderValues.ApplicationXml,
    acceptHeader: String = "application/vnd.hmrc.1.0+json"
  ): Map[CharSequence, String] =
    if (!runLocal) {
      Map(
        HttpHeaderNames.ContentType   -> contentType,
        HttpHeaderNames.Authorization -> s"Bearer $bearerToken",
        HttpHeaderNames.Accept        -> acceptHeader
      )
    } else
      Map(
        HttpHeaderNames.ContentType   -> contentType,
        HttpHeaderNames.Authorization -> bearerToken,
        HttpHeaderNames.Accept        -> acceptHeader
      )

  val tgpapiGetSuccess200: HttpRequestBuilder =
    http("GET TGP Api Record Success Response 200")
      .get(s"$baseUrl/GBWKQOZ99VLYR/records?lastUpdatedDate=2024-11-18T23:20:19Z")
      .headers(generateHeaders())
      .check(status.is(200))
}
