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

import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.TGP.TGPAPIRequest._
import uk.gov.hmrc.perftests.TGP.TGPAuthRequests.{Identifier, Identifier100, Identifier380, authLogin, getAccessTokenGG, getAuthId, getCredentialsPage, getGrantAuthority200, getGrantAuthority303, getSession, getStart, grantAuthorityRedirect, grantAuthorityRedirect2, postAuthLogin, submitGrantAuthority}

class TGPAPISimulation extends PerformanceTestRunner {

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

  setup("auth-part", "Create an access token ").withRequests(authRequests(Identifier): _*)
  setup("auth-part_100", "Create an access token ").withRequests(authRequests(Identifier100): _*)
  setup("auth-part_380", "Create an access token ").withRequests(authRequests(Identifier380): _*)

  setup("get-single-200-part", "TGP Get Single Record API Success Response 200")
    .withRequests(tgpapiGetSuccess200)

  setup("get-page-100-part", "GET TGP API Records by Page (100) - Success Response 200")
    .withRequests(tgpapiGetRecordsPage100)

  setup("get-page-380-part", "GET TGP API Records by Page (380) - Success Response 200")
    .withRequests(tgpapiGetRecordsPage380)

  setup("get-size-100-part", "GET TGP API Records by Size (100) - Success Response 200")
    .withRequests(tgpapiGetRecordsSize100)

  setup("get-size-380-part", "GET TGP API Records by Size (380) - Success Response 200")
    .withRequests(tgpapiGetRecordsSize380)

  setup("get-updated-100-part", "GET TGP API Records by Last Updated Date (100) - Success Response 200")
    .withRequests(tgpapiGetRecordsLastUpdatedDate100)

  setup("get-updated-380-part", "GET TGP API Records by Last Updated Date (380) - Success Response 200")
    .withRequests(tgpapiGetRecordsLastUpdatedDate380)

  setup("get-eori-100-part", "GET TGP API Records by EORI (100) - Success Response 200")
    .withRequests(tgpapiGetEori100)

  setup("get-eori-380-part", "GET TGP API Records by EORI (380) - Success Response 200")
    .withRequests(tgpapiGetEori380)

  setup("create-201-part", "CREATE TGP API Record Success Response 201")
    .withRequests(tgpapiCreateSuccess201)

  setup("update-200-part", "UPDATE TGP API Record Success Response 200")
    .withRequests(tgpapiUpdateSuccess200)

  setup("remove-200-part", "REMOVE TGP API Record Success Response 200")
    .withRequests(tgpapiRemoveSuccess200)

  runSimulation()
}
