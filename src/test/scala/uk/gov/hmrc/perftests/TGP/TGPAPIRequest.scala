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
import io.netty.util.AsciiString
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object TGPAPIRequest extends ServicesConfiguration {

  val authLoginStub: String = "/auth-login-stub"
  val bearerToken: String   = readProperty("bearerToken", "${accessToken}")
  val baseUrl: String       =
    if (!runLocal) s"${baseUrlFor("tgp-api")}/customs/traders/goods-profiles"
    else baseUrlFor("tgp-api")

  val CONTENT_TYPE_HEADER: AsciiString  = AsciiString.cached("Content-Type")
  val ACCEPT_HEADER: AsciiString        = AsciiString.cached("Accept")
  val AUTHORIZATION_HEADER: AsciiString = AsciiString.cached("Authorization")
  val X_CLIENT_ID_HEADER: AsciiString   = AsciiString.cached("X-Client-ID")

  // Default header values
  val APPLICATION_JSON: String     = "application/json"
  val CUSTOM_ACCEPT_HEADER: String = "application/vnd.hmrc.1.0+json"

  def generateHeaders(
    contentType: String = APPLICATION_JSON,
    acceptHeader: String = CUSTOM_ACCEPT_HEADER
  ): Map[String, String] =
    if (!runLocal)
      Map(
        CONTENT_TYPE_HEADER.toString  -> contentType,
        AUTHORIZATION_HEADER.toString -> s"Bearer $bearerToken",
        ACCEPT_HEADER.toString        -> acceptHeader
      )
    else
      Map(
        CONTENT_TYPE_HEADER.toString  -> contentType,
        AUTHORIZATION_HEADER.toString -> bearerToken,
        ACCEPT_HEADER.toString        -> acceptHeader,
        X_CLIENT_ID_HEADER.toString   -> "test"
      )

  def generateHeadersWithoutContentType(
    acceptHeader: String = CUSTOM_ACCEPT_HEADER
  ): Map[String, String] =
    if (!runLocal)
      Map(
        AUTHORIZATION_HEADER.toString -> s"Bearer $bearerToken",
        ACCEPT_HEADER.toString        -> acceptHeader
      )
    else
      Map(
        AUTHORIZATION_HEADER.toString -> bearerToken,
        ACCEPT_HEADER.toString        -> acceptHeader,
        X_CLIENT_ID_HEADER.toString   -> "test"
      )

  val getSingleGoodsRecord: HttpRequestBuilder =
    http("TGP GET single Record Api Success Response 200")
      .get(s"$baseUrl/GB123456789001/records/b2fa315b-2d31-4629-90fc-a7b1a5119873")
      .headers(generateHeadersWithoutContentType())
      .check(status.is(200))

  val createGoodsRecords: HttpRequestBuilder =
    http("CREATE TGP Api Record Success Response 201")
      .post(s"$baseUrl/GB123456789001/records")
      .headers(generateHeaders())
      .body(StringBody(Helper.jsonBody))
      .asJson
      .check(status.is(201))

  val updateGoodsRecords: HttpRequestBuilder =
    http("UPDATE TGP Api Record Success Response 200")
      .patch(s"$baseUrl/GB123456789001/records/8ebb6b04-6ab0-4fe2-ad62-e6389a8a204f")
      .headers(generateHeaders())
      .body(StringBody(Helper.jsonBody))
      .asJson
      .check(status.is(200))

  val removeGoodsRecords: HttpRequestBuilder =
    http("REMOVE TGP Api Record Success Response 204")
      .delete(s"$baseUrl/GB123456789001/records/8ebb6b04-6ab0-4fe2-ad62-e6389a8a204f?actorId=GB123456789001")
      .headers(generateHeaders())
      .asJson
      .check(status.is(204))

  val askHmrcAdvice: HttpRequestBuilder =
    http("Ask HMRC advice Success Response 201")
      .post(s"$baseUrl/GB123456789001/records/8ebb6b04-6ab0-4fe2-ad62-e6389a8a204f/advice")
      .headers(generateHeaders())
      .body(StringBody(Helper.jsonBodyAskHmrcAdvice))
      .asJson
      .check(status.is(201))

  val maintainGoodsProfile: HttpRequestBuilder =
    http("Maintain goods profile Success Response 200")
      .put(s"$baseUrl/GB123456789001")
      .headers(generateHeaders())
      .body(StringBody(Helper.jsonBodyMaintainGoodsProfile))
      .asJson
      .check(status.is(200))

  // Additional GET requests

  val get100GoodsRecordsByPage: HttpRequestBuilder =
    http("GET TGP API Records by Page (100) Success Response 200")
      .get(s"$baseUrl/GB123456789011/records?page=1")
      .headers(generateHeadersWithoutContentType())
      .check(status.is(200))

  val get380GoodsRecordsByPage: HttpRequestBuilder =
    http("GET TGP API Records by Page (380) Success Response 200")
      .get(s"$baseUrl/GB123456789012/records?page=1")
      .headers(generateHeadersWithoutContentType())
      .check(status.is(200))

  val get100GoodsRecordsBySize: HttpRequestBuilder =
    http("GET TGP API Records by Size (100) Success Response 200")
      .get(s"$baseUrl/GB123456789011/records?size=100")
      .headers(generateHeadersWithoutContentType())
      .check(status.is(200))

  val get380GoodsRecordsBySize: HttpRequestBuilder =
    http("GET TGP API Records by Size (380) Success Response 200")
      .get(s"$baseUrl/GB123456789012/records?size=380")
      .headers(generateHeadersWithoutContentType())
      .check(status.is(200))

  val get100GoodsRecordsByDate: HttpRequestBuilder =
    http("GET TGP API Records by Last Updated Date (100) Success Response 200")
      .get(s"$baseUrl/GB123456789011/records?lastUpdatedDate=2024-03-26T16:14:52Z")
      .headers(generateHeadersWithoutContentType())
      .check(status.is(200))

  val get380GoodsRecordsByDate: HttpRequestBuilder =
    http("GET TGP API Records by Last Updated Date (380) Success Response 200")
      .get(s"$baseUrl/GB123456789012/records?lastUpdatedDate=2024-03-26T16:14:52Z")
      .headers(generateHeadersWithoutContentType())
      .check(status.is(200))

  val get100GoodsRecordsByEORI: HttpRequestBuilder =
    http("GET TGP API Records by EORI (100) Success Response 200")
      .get(s"$baseUrl/GB123456789011/records")
      .headers(generateHeadersWithoutContentType())
      .check(status.is(200))

  val get380GoodsRecordsByEORI: HttpRequestBuilder =
    http("GET TGP API Records by EORI (380) Success Response 200")
      .get(s"$baseUrl/GB123456789012/records")
      .headers(generateHeadersWithoutContentType())
      .check(status.is(200))

}
