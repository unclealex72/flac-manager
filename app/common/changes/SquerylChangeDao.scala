/*
 * Copyright 2014 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package common.changes

import common.changes.ChangeSchema._
import common.changes.SquerylEntryPoint._
import common.configuration.User
import org.joda.time.DateTime

/**
 * The Squeryl implementation of both GameDao and Transactional.
 * @author alex
 *
 */
class SquerylChangeDao extends ChangeDao with Transactional {

  /**
   * Run code within a transaction.
   * @param block The code to run.
   */
  def tx[T](block: ChangeDao => T): T = inTransaction {
    block(this)
  }

  def store(change: Change): Change = inTransaction {
    changes.insertOrUpdate(change)
    change
  }

  override def getAllChangesSince(user: User, since: DateTime): List[Change] = inTransaction {
    changes.where(c => c.user === user.name and c.at >= since)
  }

}