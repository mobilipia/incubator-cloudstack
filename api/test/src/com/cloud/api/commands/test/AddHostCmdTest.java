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
package src.com.cloud.api.commands.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.cloud.api.ResponseGenerator;
import com.cloud.api.ServerApiException;
import com.cloud.api.commands.AddHostCmd;
import com.cloud.api.response.HostResponse;
import com.cloud.api.response.ListResponse;
import com.cloud.exception.DiscoveryException;
import com.cloud.exception.InvalidParameterValueException;
import com.cloud.host.Host;
import com.cloud.resource.ResourceService;

import edu.emory.mathcs.backport.java.util.Arrays;

public class AddHostCmdTest extends TestCase {

    private AddHostCmd addHostCmd;
    private ResourceService resourceService;
    private ResponseGenerator responseGenerator;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        resourceService = Mockito.mock(ResourceService.class);
        responseGenerator = Mockito.mock(ResponseGenerator.class);
        addHostCmd = new AddHostCmd() {
        };
    }

    @Test
    public void testExecuteForEmptyResult() {
        addHostCmd._resourceService = resourceService;

        try {
            addHostCmd.execute();
        } catch (ServerApiException exception) {
            Assert.assertEquals("Failed to add host",
                    exception.getDescription());
        }

    }

    @Test
    public void testExecuteForNullResult() {

        ResourceService resourceService = Mockito.mock(ResourceService.class);
        addHostCmd._resourceService = resourceService;

        try {
            Mockito.when(resourceService.discoverHosts(addHostCmd)).thenReturn(
                    null);
        } catch (InvalidParameterValueException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DiscoveryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            addHostCmd.execute();
        } catch (ServerApiException exception) {
            Assert.assertEquals("Failed to add host",
                    exception.getDescription());
        }

    }

    /*
     * @Test public void testExecuteForResult() throws Exception {
     * 
     * addHostCmd._resourceService = resourceService;
     * addHostCmd._responseGenerator = responseGenerator; MockHost mockInstance
     * = new MockHost(); MockHost[] mockArray = new MockHost[]{mockInstance};
     * HostResponse responseHost = new HostResponse();
     * responseHost.setName("Test");
     * Mockito.when(resourceService.discoverHosts(addHostCmd
     * )).thenReturn(Arrays.asList(mockArray));
     * Mockito.when(responseGenerator.createHostResponse
     * (mockInstance)).thenReturn(responseHost); addHostCmd.execute();
     * Mockito.verify(responseGenerator).createHostResponse(mockInstance);
     * ListResponse<HostResponse> actualResponse =
     * ((ListResponse<HostResponse>)addHostCmd.getResponseObject());
     * Assert.assertEquals(responseHost, actualResponse.getResponses().get(0));
     * Assert.assertEquals("addhostresponse", actualResponse.getResponseName());
     * }
     */
    @Test
    public void testExecuteForResult() throws Exception {

        addHostCmd._resourceService = resourceService;
        addHostCmd._responseGenerator = responseGenerator;
        Host host = Mockito.mock(Host.class);
        Host[] mockArray = new Host[] { host };

        HostResponse responseHost = new HostResponse();
        responseHost.setName("Test");
        Mockito.when(resourceService.discoverHosts(addHostCmd)).thenReturn(
                Arrays.asList(mockArray));
        Mockito.when(responseGenerator.createHostResponse(host)).thenReturn(
                responseHost);
        addHostCmd.execute();
        Mockito.verify(responseGenerator).createHostResponse(host);
        ListResponse<HostResponse> actualResponse = ((ListResponse<HostResponse>) addHostCmd
                .getResponseObject());
        Assert.assertEquals(responseHost, actualResponse.getResponses().get(0));
        Assert.assertEquals("addhostresponse", actualResponse.getResponseName());

    }

    @Test
    public void testExecuteForDiscoveryException() {

        addHostCmd._resourceService = resourceService;

        try {
            Mockito.when(resourceService.discoverHosts(addHostCmd)).thenThrow(
                    DiscoveryException.class);
        } catch (InvalidParameterValueException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (DiscoveryException e) {
            e.printStackTrace();
        }

        try {
            addHostCmd.execute();
        } catch (ServerApiException exception) {
            Assert.assertNull(exception.getDescription());
        }

    }

}
