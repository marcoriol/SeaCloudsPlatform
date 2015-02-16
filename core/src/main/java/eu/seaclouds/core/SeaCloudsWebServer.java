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

package eu.seaclouds.core;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;

public class SeaCloudsWebServer {

    private static final Logger log = LoggerFactory.getLogger(SeaCloudsWebServer.class);

    private SeaCloudsWebServer serverInstance;

    private Server server;

    private InetAddress bindAddress;

    private InetAddress publicAddress;

    private InetAddress actualAddress;

    private int portNumber = 8080;

    private String warPath;

    private SeaCloudsWebServer(){
        server = new Server();
    }

    public static SeaCloudsWebServer newInstance(){
        return new SeaCloudsWebServer();
    }

    public SeaCloudsWebServer setBindAddress(InetAddress address){
        bindAddress = address;
        return this;
    }

    public SeaCloudsWebServer setPublicAddress(InetAddress address){
        publicAddress = address;
        return this;
    }

    public InetAddress getAddress() {
        return publicAddress;
    }

    public String getRootUrl() {
        if (publicAddress != null) {
            String address = publicAddress.getHostName();
            if (address != null && portNumber > 0){
                String protocol = "http";
                return protocol+"://"+address+":"+portNumber+"/";
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public SeaCloudsWebServer setPort(int port){
        portNumber = port;
        return this;
    }

    public SeaCloudsWebServer setWarPath(String warPath){
        URL warResource = getClass().getResource(warPath);
        if (warResource != null) {
            this.warPath = warResource.getPath();
        } else {
            log.warn("War path not found: " + warPath);
        }
        return this;
    }

    public void start(){
        try {
            server = new Server(new InetSocketAddress(bindAddress, portNumber));
            if (bindAddress == null || bindAddress.equals(InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 }))) {
                actualAddress = InetAddress.getLocalHost();
            } else {
                actualAddress = bindAddress;
            }

            WebAppContext rootContext = new WebAppContext();
            rootContext.setContextPath("/");
            rootContext.setWar(warPath);

            server.setHandler(rootContext);
            server.start();

        } catch (Exception e) {
            log.error("War path not found: " + warPath);
            server.destroy();
        }
    }

    public void stop(){

    }


}
