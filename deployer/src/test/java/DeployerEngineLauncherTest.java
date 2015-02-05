/**
 * Copyright 2014 SeaClouds
 * Contact: mbarrientos@lcc.uma.es
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import seaclouds.deployer.DeployerEngineLauncher;

import static org.testng.Assert.*;

public class DeployerEngineLauncherTest {

    DeployerEngineLauncher deployer;

    @org.testng.annotations.BeforeMethod
    public void setUp() throws Exception {
        deployer = DeployerEngineLauncher.newInstance();
    }

    @org.testng.annotations.AfterMethod
    public void tearDown() throws Exception {
        deployer.stop();
    }

    @org.testng.annotations.Test
    public void testStart() throws Exception {
        deployer.start();
    }
}