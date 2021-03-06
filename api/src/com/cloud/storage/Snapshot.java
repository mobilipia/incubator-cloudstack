// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.storage;

import java.util.Date;

import com.cloud.acl.ControlledEntity;
import com.cloud.hypervisor.Hypervisor.HypervisorType;

public interface Snapshot extends ControlledEntity {
    public enum Type {
        MANUAL,
        RECURRING,
        TEMPLATE,
        HOURLY,
        DAILY,
        WEEKLY,
        MONTHLY;
        private int max = 8;

        public void setMax(int max) {
            this.max = max;
        }

        public int getMax() {
            return max;
        }

        public String toString() {
            return this.name();
        }

        public boolean equals(String snapshotType) {
            return this.toString().equalsIgnoreCase(snapshotType);
        }
    }

    public enum Status {
        Creating,
        CreatedOnPrimary,
        BackingUp,
        BackedUp,
        Error;

        public String toString() {
            return this.name();
        }

        public boolean equals(String status) {
            return this.toString().equalsIgnoreCase(status);
        }
    }

    public static final long MANUAL_POLICY_ID = 0L;

    Long getId();

    long getAccountId();

    long getVolumeId();

    String getPath();

    String getName();

    Date getCreated();

    Type getType();

    Status getStatus();

    HypervisorType getHypervisorType();

    boolean isRecursive();

    short getsnapshotType();

}
