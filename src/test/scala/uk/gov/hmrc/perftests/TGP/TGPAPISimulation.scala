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

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.TGP.TGPAPIRequest._
import uk.gov.hmrc.perftests.TGP.TGPAuthRequests.{EORI, EORIFor100Records, EORIFor380Records}
import uk.gov.hmrc.perftests.TGP.setup.Setup.setupSession

class TGPAPISimulation extends PerformanceTestRunner {

  setup(
    "trader-goods-profiles-Get-Create-Update-Remove-Ask-advice-Maintain-profile-record-part",
    "Get, Create, Update, Remove, Ask HMRC advice and Maintain goods profile - Success Response 200"
  ).withActions(setupSession(EORI): _*)
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
  ).withActions(setupSession(EORIFor100Records): _*)
    .withRequests(
      get100GoodsRecordsByPage,
      get100GoodsRecordsBySize,
      get100GoodsRecordsByDate,
      get100GoodsRecordsByEORI
    )

  setup(
    "get-380-goods-records-by-page-size-date-EORI-part",
    "GET (380) Records by Page,Size,Date,EORI - Success Response 200"
  ).withActions(setupSession(EORIFor380Records): _*)
    .withRequests(
      get380GoodsRecordsByPage,
      get380GoodsRecordsBySize,
      get380GoodsRecordsByDate,
      get380GoodsRecordsByEORI
    )

  runSimulation()
}
