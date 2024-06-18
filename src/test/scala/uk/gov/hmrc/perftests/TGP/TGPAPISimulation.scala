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
import uk.gov.hmrc.perftests.TGP.TGPAuthRequests.{EORI, EORIFor100Records, EORIFor380Records, authLogin, getAccessTokenGG, getAuthId, getCredentialsPage, getGrantAuthority200, getGrantAuthority303, getSession, getStart, grantAuthorityRedirect, grantAuthorityRedirect2, postAuthLogin, submitGrantAuthority}

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

  setup("auth-part", "Create an access token ").withRequests(authRequests(EORI): _*)
  setup("auth-part-get-100-goods-records", "Create an access token ").withRequests(authRequests(EORIFor100Records): _*)
  setup("auth-part-get-380-goods-records", "Create an access token ").withRequests(authRequests(EORIFor380Records): _*)

  setup(
    "trader-goods-profiles-Get-Create-Update-Remove-Ask advice-Maintain profile-record-part",
    "Get,Create,Update, Remove,Ask HMRC advice and Maintain goods profile- Success Response 200"
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

  setup("get-single-goods-record-part", "Get Single Record - Success Response 200")
    .withRequests(getSingleGoodsRecord)

  setup("get-100-goods-records-by-page-part", "GET (100) Records by Page - Success Response 200")
    .withRequests(get100GoodsRecordsByPage)

  setup("get-380-goods-records-by-page-part", "GET (380) goods records by Page - Success Response 200")
    .withRequests(get380GoodsRecordsByPage)

  setup("get-100-goods-records-by-size-part", "GET (100) goods records by Size - Success Response 200")
    .withRequests(get100GoodsRecordsBySize)

  setup("get-380-goods-records-by-size-part", "GET (380) goods records by Size - Success Response 200")
    .withRequests(get380GoodsRecordsBySize)

  setup("get-100-goods-records-by-date-part", "GET (100) goods Records by Date - Success Response 200")
    .withRequests(get100GoodsRecordsByDate)

  setup("get-380-goods-records-by-date-part", "GET (380) goods records by Date - Success Response 200")
    .withRequests(get380GoodsRecordsByDate)

  setup("get-100-goods-records-by-EORI-part", "GET (100) goods records by EORI - Success Response 200")
    .withRequests(get100GoodsRecordsByEORI)

  setup("get-380-goods-records-by-EORI-part", "GET (380) goods records by EORI - Success Response 200")
    .withRequests(get380GoodsRecordsByEORI)

  setup("create-goods-records-part", "CREATE goods records - Success Response 201")
    .withRequests(createGoodsRecords)

  setup("update-goods-records-part", "UPDATE goods records - Success Response 200")
    .withRequests(updateGoodsRecords)

  setup("remove-goods-records-part", "REMOVE goods records - Success Response 204")
    .withRequests(removeGoodsRecords)

  setup("ask-hmrc-advice-part", "Ask HMRC advice - Success Response 201")
    .withRequests(askHmrcAdvice)

  setup("maintain-goods-profile-part", "Maintain goods profile - Success Response 200")
    .withRequests(maintainGoodsProfile)

  runSimulation()
}
