/**
 * Copyright 2022- IBM Inc. All rights reserved
 * SPDX-License-Identifier: Apache2.0
 */

package org.apache.spark.shuffle

import org.apache.spark.shuffle.api._
import org.apache.spark.shuffle.helper.S3ShuffleDispatcher
import org.apache.spark.storage.BlockManagerMaster
import org.apache.spark.{SparkConf, SparkEnv}

import java.util
import java.util.Collections

class S3ShuffleDataIO(sparkConf: SparkConf) extends ShuffleDataIO {
  override def executor(): AiqShuffleExecutorComponents = new S3AiqShuffleExecutorComponents()

  override def driver(): AiqShuffleDriverComponents = new S3AiqShuffleDriverComponents()

  private class S3AiqShuffleExecutorComponents extends AiqShuffleExecutorComponents {
    private val blockManager = SparkEnv.get.blockManager

    override def initializeExecutor(appId: String, execId: String, extraConfigs: util.Map[String, String]): Unit = {
      S3ShuffleDispatcher.get.reinitialize(appId)
    }

    override def aiqCreateMapOutputWriter(shuffleId: Int, mapTaskId: Long, numPartitions: Int): AiqShuffleMapOutputWriter = {
      new S3ShuffleMapOutputWriter(sparkConf, shuffleId, mapTaskId, numPartitions)
    }

    override def aiqCreateSingleFileMapOutputWriter(
                                                  shuffleId: Int,
                                                  mapId: Long
                                                ): Option[AiqSingleSpillShuffleMapOutputWriter] = {
      Option(new S3SingleSpillShuffleMapOutputWriter(shuffleId, mapId))
    }
  }

  private class S3AiqShuffleDriverComponents extends AiqShuffleDriverComponents {
    private var blockManagerMaster: BlockManagerMaster = null

    override def initializeApplication(): util.Map[String, String] = {
      blockManagerMaster = SparkEnv.get.blockManager.master
      Collections.emptyMap()
    }

    override def cleanupApplication(): Unit = {
      // Dispatcher cleanup
      if (S3ShuffleDispatcher.get.cleanupShuffleFiles) {
        S3ShuffleDispatcher.get.removeRoot()
      }
    }

    override def registerShuffle(shuffleId: Int): Unit = {
      super.registerShuffle(shuffleId)
    }

    override def removeShuffle(shuffleId: Int, blocking: Boolean): Unit = {
      super.removeShuffle(shuffleId, blocking)
    }
  }
}

