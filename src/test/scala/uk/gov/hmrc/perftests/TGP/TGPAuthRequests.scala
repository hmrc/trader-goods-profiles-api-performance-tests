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
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.css.CssCheckType
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import jodd.lagarto.dom.NodeSelector
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.util.matching.UnanchoredRegex

object TGPAuthRequests extends ServicesConfiguration {
  lazy val CsrfPattern                  = """<input type="hidden" name="csrfToken" value="([^"]+)""""
  lazy val baseUrl_Auth: String         = baseUrlFor("auth-login-stub")
  lazy val baseUrl_Auth_Token: String   = baseUrlFor("tgp-api")
  lazy val jsonPattern: UnanchoredRegex = """\{"\w+":"([^"]+)""".r.unanchored

  lazy val clientId             = ""
  lazy val clientSecret: String = ""
  lazy val redirectUri: String  = "urn:ietf:wg:oauth:2.0:oob"
  lazy val authBaseUrl: String  = baseUrlFor("auth-login-stub")

  lazy val authUrl: String = s"$authBaseUrl/auth-login-stub/gg-sign-in"
  lazy val redirectionUrl  = s"$authBaseUrl/auth-login-stub/session"
  lazy val scope: String   = ""

  final val Identifier = "GB123456789001"

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def getAuthId: HttpRequestBuilder =
    http(requestName = "get AuthId")
      .get(
        s"$baseUrl_Auth/oauth/authorize?client_id=$clientId&redirect_uri=$redirectUri&scope=$scope&response_type=code"
      )
      .check(status.is(expected = 303))
      .check(
        header("Location")
          .transform(location => extractDynamicCode(location))
          .saveAs("auth_id")
      )
      .check(header("Location").is("/oauth/start?auth_id=${auth_id}"))

  def postAuthLogin(Eori: String): HttpRequestBuilder =
    http("Enter Auth login credentials ")
      .post(authUrl)
      .formParam("redirectionUrl", redirectionUrl)
      .formParam("authorityId", "")
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Organisation")
      .formParam("enrolment[0].name", "HMRC-CUS-ORG")
      .formParam("enrolment[0].taxIdentifier[0].name", "EORINumber")
      .formParam("enrolment[0].taxIdentifier[0].value", Eori)
      .formParam("enrolment[0].state", "Activated")
      .check(status.is(303))
      .check(bodyString.saveAs("responseBody"))

  val getSession: HttpRequestBuilder =
    http("Get auth login stub session information")
      .get(redirectionUrl)
      .check(status.is(200))
      .check(saveBearerToken)

  def saveBearerToken: CheckBuilder[CssCheckType, NodeSelector, String] =
    css("[data-session-id=authToken] > code")
      .saveAs("accessToken")

  def extractDynamicCode(location: String): String = {
    val p = """([^=]*)$""".r.unanchored
    location match {
      case p(auth_id) => auth_id
      case _          => ""
    }
  }

  def getStart: HttpRequestBuilder =
    http("get Start")
      .get(baseUrl_Auth + "/oauth/start?auth_id=${auth_id}")
      .check(status.is(200))

  def getGrantAuthority303: HttpRequestBuilder =
    http("get Grant Authority 303")
      .get(baseUrl_Auth + "/oauth/grantscope?auth_id=${auth_id}")
      .check(status.is(303))
      .check(
        header("Location")
          .is(baseUrl_Auth + "/gg/sign-in?continue=%2Foauth%2Fgrantscope%3Fauth_id%3D${auth_id}&origin=oauth-frontend")
      )

  def getCredentialsPage: HttpRequestBuilder =
    http("get credentials page")
      .get(baseUrl_Auth + "/auth-login-stub/gg-sign-in")
      .check(status.is(200))

  def authLogin(Eori: String): HttpRequestBuilder =
    http("login Step")
      .post(baseUrl_Auth + "/auth-login-stub/gg-sign-in")
      .formParam("redirectionUrl", "/oauth/grantscope?auth_id=${auth_id}")
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "200")
      .formParam("authorityId", "abcd")
      .formParam("affinityGroup", "Organisation")
      .formParam("enrolment[0].name", "HMRC-CUS-ORG")
      .formParam("enrolment[0].taxIdentifier[0].name", "EORINumber")
      .formParam("enrolment[0].taxIdentifier[0].value", Eori)
      .formParam("enrolment[0].state", "Activated")
      .check(status.is(303))

  def grantAuthorityRedirect: HttpRequestBuilder =
    http("get Grant Authority 2nd Redirect")
      .get(baseUrl_Auth + "/gg/sign-in?continue=%2Foauth%2Fgrantscope%3Fauth_id%3D${auth_id}&origin=oauth-frontend")
      .check(status.is(303))
      .check(
        header("Location")
          .is(
            baseUrl_Auth + "/bas-gateway/sign-in?continue_url=%2Foauth%2Fgrantscope%3Fauth_id%3D${auth_id}&origin=oauth-frontend"
          )
      )

  def grantAuthorityRedirect2: HttpRequestBuilder =
    http("get Grant Authority  Redirect2")
      .get(
        baseUrl_Auth + "/bas-gateway/sign-in?continue_url=%2Foauth%2Fgrantscope%3Fauth_id%3D${auth_id}&origin=oauth-frontend"
      )
      .check(status.is(303))

  def getGrantAuthority200: HttpRequestBuilder =
    http("get Grant Authority 200")
      .get(baseUrl_Auth + "/oauth/grantscope?auth_id=${auth_id}")
      .check(status.is(200))
      .check(saveCsrfToken())

  def submitGrantAuthority: HttpRequestBuilder =
    http("submit Grant Authority")
      .post(baseUrl_Auth + "/oauth/grantscope": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("auth_id", "${auth_id}")
      .check(status.is(200))
      .check(css("#authorisation-code").ofType[String].saveAs("code"))

  def getAccessTokenGG: HttpRequestBuilder =
    http("Retrieve Access Token through GG")
      .post(s"$baseUrl_Auth_Token/oauth/token")
      .headers(Map("Content-Type" -> "application/x-www-form-urlencoded"))
      .body(
        StringBody(
          "code=${code}&client_id=" + clientId +
            "&client_secret=" + clientSecret +
            "&grant_type=authorization_code&redirect_uri=" + redirectUri
        )
      )
      .check(
        bodyString
          .transform { (body: String) =>
            body match {
              case jsonPattern(access_token) => access_token
              case _                         => ""
            }
          }
          .saveAs("accessToken")
      )
      .check(status.is(200))

}
