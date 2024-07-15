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
import uk.gov.hmrc.perftests.TGP.TGPAuthRequests._

class TGPAPISimulation extends PerformanceTestRunner {

  def authRequests(EORI: String): Seq[HttpRequestBuilder] =
    if (!runLocal) {
      Seq(
        getAuthId,
        getStart,
        getGrantAuthority303,
        getCredentialsPage,
        authLogin(EORI),
        grantAuthorityRedirect,
        grantAuthorityRedirect2,
        getGrantAuthority200,
        submitGrantAuthority,
        getAccessTokenGG
      )
    } else {
      Seq(postAuthLogin(EORI), getSession)
    }

  before(
    authRequests(EORI)
  )

//  setup("auth-part", "Create an access token ").withRequests(authRequests(EORI): _*)
//  setup("auth-part-get-100-goods-records", "Create an access token ").withRequests(authRequests(EORIFor100Records): _*)
//  setup("auth-part-get-380-goods-records", "Create an access token ").withRequests(authRequests(EORIFor380Records): _*)

  setup(
    "trader-goods-profiles-Get-Create-Update-Remove-Ask-advice-Maintain-profile-record-part",
    "Get, Create, Update, Remove, Ask HMRC advice and Maintain goods profile - Success Response 200"
  )
    .withRequests(
      getSingleGoodsRecord,
      createGoodsRecords,
      updateGoodsRecords,
      removeGoodsRecords,
      askHmrcAdvice,
      maintainGoodsProfile
    )

  setup(
    "get-100-goods-records-by-page-size-date-EORI-part",
    "GET (100) Records by Page,Size,Date,EORI - Success Response 200"
  )
    .withRequests(
      get100GoodsRecordsByPage,
      get100GoodsRecordsBySize,
      get100GoodsRecordsByDate,
      get100GoodsRecordsByEORI
    )

  setup(
    "get-380-goods-records-by-page-size-date-EORI-part",
    "GET (380) Records by Page,Size,Date,EORI - Success Response 200"
  )
    .withRequests(
      get380GoodsRecordsByPage,
      get380GoodsRecordsBySize,
      get380GoodsRecordsByDate,
      get380GoodsRecordsByEORI
    )

  runSimulation()
}
