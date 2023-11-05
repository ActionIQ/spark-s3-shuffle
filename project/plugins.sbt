//
// Copyright 2022- IBM Inc. All rights reserved
// SPDX-License-Identifier: Apache2.0
//
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.2.0") // https://github.com/sbt/sbt-assembly (MIT)
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.11.0") // https://github.com/sbt/sbt-buildinfo (MIT)
addSbtPlugin("com.github.sbt" % "sbt-git" % "2.0.1") // https://github.com/sbt/sbt-git (BSD-2-Clause)

// Custom version of fm-sbt-s3-resolver published to our Artifactory for DEVX-275
// See repo here: https://github.com/ActionIQ/sbt-s3-resolver
addSbtPlugin("co.actioniq" % "sbt-s3-resolver" % "1.0.1")