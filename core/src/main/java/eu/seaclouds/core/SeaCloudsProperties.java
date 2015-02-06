/**
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
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
package eu.seaclouds.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SeaCloudsProperties {
    public final static String MONITOR_ENDPOINT = "monitor.url";
    public final static String DEPLOYER_ENDPOINT = "deployer.url";
    public final static String SLA_ENDPOINT = "sla.url";

    final static String PROPERTIES_PATH = "../seaclouds.properties";

    public static String get(String property){
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(PROPERTIES_PATH);
            prop.load(input);
            return prop.getProperty(property);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
