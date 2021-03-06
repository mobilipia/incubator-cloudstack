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
package com.cloud.api.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.cloud.api.ApiConstants;
import com.cloud.api.BaseListCmd;
import com.cloud.api.IdentityMapper;
import com.cloud.api.Implementation;
import com.cloud.api.Parameter;
import com.cloud.api.response.ListResponse;
import com.cloud.api.response.SnapshotPolicyResponse;
import com.cloud.storage.snapshot.SnapshotPolicy;

@Implementation(description="Lists snapshot policies.", responseObject=SnapshotPolicyResponse.class)
public class ListSnapshotPoliciesCmd extends BaseListCmd {
    public static final Logger s_logger = Logger.getLogger(ListSnapshotPoliciesCmd.class.getName());

    private static final String s_name = "listsnapshotpoliciesresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @IdentityMapper(entityTableName="volumes")
    @Parameter(name=ApiConstants.VOLUME_ID, type=CommandType.LONG, required=true, description="the ID of the disk volume")
    private Long volumeId;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Long getVolumeId() {
        return volumeId;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public void execute(){
        List<? extends SnapshotPolicy> result = _snapshotService.listPoliciesforVolume(this);
        ListResponse<SnapshotPolicyResponse> response = new ListResponse<SnapshotPolicyResponse>();
        List<SnapshotPolicyResponse> policyResponses = new ArrayList<SnapshotPolicyResponse>();
        for (SnapshotPolicy policy : result) {
            SnapshotPolicyResponse policyResponse = _responseGenerator.createSnapshotPolicyResponse(policy);
            policyResponse.setObjectName("snapshotpolicy");
            policyResponses.add(policyResponse);
        }
        response.setResponses(policyResponses);
        response.setResponseName(getCommandName());   
        this.setResponseObject(response);
    }
}
