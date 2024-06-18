#!/usr/bin/env bash
sbt scalafmtCheckAll scalafmtCheck
sbt scalafmtSbt
sbt scalafmtAll
sbt -DrunLocal=true -Dperftest.runSmokeTest=false Gatling/test
