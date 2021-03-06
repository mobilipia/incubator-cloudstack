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
import com.cloud.api.BaseAsyncCreateCmd;
import com.cloud.api.BaseCmd;
import com.cloud.api.IdentityMapper;
import com.cloud.api.Implementation;
import com.cloud.api.Parameter;
import com.cloud.api.ServerApiException;
import com.cloud.api.response.VpnUsersResponse;
import com.cloud.domain.Domain;
import com.cloud.event.EventTypes;
import com.cloud.network.VpnUser;
import com.cloud.user.Account;
import com.cloud.user.UserContext;

@Implementation(description="Adds vpn users", responseObject=VpnUsersResponse.class)
public class AddVpnUserCmd extends BaseAsyncCreateCmd {
    public static final Logger s_logger = Logger.getLogger(AddVpnUserCmd.class.getName());

    private static final String s_name = "addvpnuserresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////
    @Parameter(name=ApiConstants.USERNAME, type=CommandType.STRING, required=true, description="username for the vpn user")
    private String userName;
    
    @Parameter(name=ApiConstants.PASSWORD, type=CommandType.STRING, required=true, description="password for the username")
    private String password;
    
    @Parameter(name=ApiConstants.ACCOUNT, type=CommandType.STRING, description="an optional account for the vpn user. Must be used with domainId.")
    private String accountName;
    
    @IdentityMapper(entityTableName="projects")
    @Parameter(name=ApiConstants.PROJECT_ID, type=CommandType.LONG, description="add vpn user to the specific project")
    private Long projectId;

    @IdentityMapper(entityTableName="domain")
    @Parameter(name=ApiConstants.DOMAIN_ID, type=CommandType.LONG, description="an optional domainId for the vpn user. If the account parameter is used, domainId must also be used.")
    private Long domainId;
    
    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////


	public String getAccountName() {
		return accountName;
	}

	public Long getDomainId() {
		return domainId;
	}

	public String getUserName() {
		return userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public Long getProjectId() {
	    return projectId;
	}
	
    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

	@Override
    public String getCommandName() {
        return s_name;
    }

	@Override
	public long getEntityOwnerId() {
        Long accountId = finalyzeAccountId(accountName, domainId, projectId, true);
        if (accountId == null) {
            return UserContext.current().getCaller().getId();
        }
        
        return accountId;
    }
	
    public String getEntityTable() {
    	return "vpn_users";
    }

	@Override
	public String getEventDescription() {
		return "Add Remote Access VPN user for account " + getEntityOwnerId() + " username= " + getUserName();
	}

	@Override
	public String getEventType() {
		return EventTypes.EVENT_VPN_USER_ADD;
	}

    @Override
    public void execute(){
            VpnUser vpnUser = _entityMgr.findById(VpnUser.class, getEntityId());
            Account account = _entityMgr.findById(Account.class, vpnUser.getAccountId());
            if (!_ravService.applyVpnUsers(vpnUser.getAccountId(), userName)) {
                throw new ServerApiException(BaseCmd.INTERNAL_ERROR, "Failed to add vpn user");
            }
            
            VpnUsersResponse vpnResponse = new VpnUsersResponse();
            vpnResponse.setId(vpnUser.getId());
            vpnResponse.setUserName(vpnUser.getUsername());
            vpnResponse.setAccountName(account.getAccountName());
            
            vpnResponse.setDomainId(account.getDomainId());
            vpnResponse.setDomainName(_entityMgr.findById(Domain.class, account.getDomainId()).getName());
            
            vpnResponse.setResponseName(getCommandName());
            vpnResponse.setObjectName("vpnuser");
            this.setResponseObject(vpnResponse);
    }

    @Override
    public void create() {
        Account owner = _accountService.getAccount(getEntityOwnerId());
     
        VpnUser vpnUser = _ravService.addVpnUser(owner.getId(), userName, password);
        if (vpnUser == null) {
            throw new ServerApiException(BaseCmd.INTERNAL_ERROR, "Failed to add vpn user");
        }
        setEntityId(vpnUser.getId());
    }	
}
