#!/usr/bin/env bash
sbt scalafmtCheckAll scalafmtCheck
sbt scalafmtSbt
sbt scalafmtAll
sbt -DrunLocal=false -Dperftest.runSmokeTest=false Gatling/test
