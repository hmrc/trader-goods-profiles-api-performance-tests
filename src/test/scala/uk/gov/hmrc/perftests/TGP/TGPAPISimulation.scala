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
import uk.gov.hmrc.perftests.TGP.TGPAuthRequests.{Identifier, authLogin, getAccessTokenGG, getAuthId, getCredentialsPage, getGrantAuthority200, getGrantAuthority303, getSession, getStart, grantAuthorityRedirect, grantAuthorityRedirect2, postAuthLogin, submitGrantAuthority}

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

  setup("tgp-api-get-single-goods-record-success-200-part", "TGP Get Single Record APi Success Response 200")
    .withRequests(tgpapiGetSuccess200)

  setup("tgp-api-get-records-page-part", "GET TGP Api Records by Page -Success Response 200 ")
    .withRequests(tgpapiGetRecordsPage)

  setup("tgp-api-get-records-size-part", "GET TGP Api Records by Size -Success Response 200")
    .withRequests(tgpapiGetRecordsSize)

  setup("tgp-api-get-records-last-updated-date-part", "GET TGP Api Records by Last Updated Date -Success Response 200")
    .withRequests(tgpapiGetRecordsLastUpdatedDate)

  setup("tgp-api-get-record-EORI-part", "GET TGP Api Records by EORI -Success Response 200")
    .withRequests(tgpapiGetEori)

  setup("tgp-api-create-success-201-part", "CREATE TGP Api Record Success Response 201")
    .withRequests(tgpapiCreateSuccess201)

  setup("tgp-api-update-success-200-part", "UPDATE TGP Api Record Success Response 200")
    .withRequests(tgpapiUpdateSuccess200)

  setup("tgp-api-remove-success-200-part", "REMOVE TGP Api Record Success Response 200")
    .withRequests(tgpapiRemoveSuccess200)

  runSimulation()
}
