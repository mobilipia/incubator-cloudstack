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

import org.apache.log4j.Logger;

import com.cloud.api.ApiConstants;
import com.cloud.api.BaseAsyncCmd;
import com.cloud.api.BaseCmd;
import com.cloud.api.IdentityMapper;
import com.cloud.api.Implementation;
import com.cloud.api.Parameter;
import com.cloud.api.PlugService;
import com.cloud.api.ServerApiException;
import com.cloud.api.response.VirtualRouterProviderResponse;
import com.cloud.network.VirtualRouterProvider;
import com.cloud.network.element.VirtualRouterElementService;
import com.cloud.async.AsyncJob;
import com.cloud.event.EventTypes;
import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.ResourceUnavailableException;
import com.cloud.user.Account;
import com.cloud.user.UserContext;

@Implementation(responseObject=VirtualRouterProviderResponse.class, description="Configures a virtual router element.")
public class ConfigureVirtualRouterElementCmd extends BaseAsyncCmd {
	public static final Logger s_logger = Logger.getLogger(ConfigureVirtualRouterElementCmd.class.getName());
    private static final String s_name = "configurevirtualrouterelementresponse";
    
    @PlugService
    private VirtualRouterElementService _service;

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @IdentityMapper(entityTableName = "virtual_router_providers")
    @Parameter(name=ApiConstants.ID, type=CommandType.LONG, required=true, description="the ID of the virtual router provider")
    private Long id;

    @IdentityMapper(entityTableName = "physical_network_service_providers")
    @Parameter(name=ApiConstants.ENABLED, type=CommandType.BOOLEAN, required=true, description="Enabled/Disabled the service provider")
    private Boolean enabled;
    
    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }
    
    public static String getResultObjectName() {
    	return "boolean";
    }
    
    @Override
    public long getEntityOwnerId() {
        return Account.ACCOUNT_ID_SYSTEM;
    }

    @Override
    public String getEventType() {
        return EventTypes.EVENT_NETWORK_ELEMENT_CONFIGURE;
    }

    @Override
    public String getEventDescription() {
        return  "configuring virtual router provider: " + id;
    }
    
    public AsyncJob.Type getInstanceType() {
    	return AsyncJob.Type.None;
    }
    
    public Long getInstanceId() {
        return id;
    }
	
    @Override
    public void execute() throws ConcurrentOperationException, ResourceUnavailableException, InsufficientCapacityException{
        UserContext.current().setEventDetails("Virtual router element: " + id);
        VirtualRouterProvider result = _service.configure(this);
        if (result != null){
            VirtualRouterProviderResponse routerResponse = _responseGenerator.createVirtualRouterProviderResponse(result);
            routerResponse.setResponseName(getCommandName());
            this.setResponseObject(routerResponse);
        } else {
            throw new ServerApiException(BaseCmd.INTERNAL_ERROR, "Failed to configure the virtual router provider");
        }
    }
}
