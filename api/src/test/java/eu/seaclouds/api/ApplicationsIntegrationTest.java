/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.seaclouds.api;

import brooklyn.rest.client.BrooklynApi;
import eu.seaclouds.core.SeaCloudsProperties;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class ApplicationsIntegrationTest {

    String endpoint;
    BrooklynApi brooklyn;

    @BeforeClass
    public void before(){
        endpoint = SeaCloudsProperties.get(SeaCloudsProperties.DEPLOYER_ENDPOINT);
        if (endpoint == null){
            Assert.fail("Deployer endpoint is not specified");
        }

        try {
            brooklyn = new BrooklynApi(endpoint);
        } catch (Exception e) {
            Assert.fail("Couldn't connect to Brooklyn endpoint: " + endpoint);
        }
    }

    @Test(groups = { "integration" })
    public void testListApplications() throws Exception {

    }

    @Test(groups = { "integration" })
    public void testAddApplication() throws Exception {

    }

    @Test(groups = { "integration" })
    public void testRemoveApplication() throws Exception {

    }
}