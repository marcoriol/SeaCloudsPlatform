/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
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
package seaclouds.deployer.api;

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import brooklyn.rest.domain.EntitySummary;
import brooklyn.rest.domain.LocationSummary;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import eu.seaclouds.platform.dashboard.SeaCloudsProperties;
import org.jboss.resteasy.client.ClientResponseFailure;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.xml.ws.Response;

@Path("/application")
public class Application {

    final static BrooklynApi BROOKLKYN_API = new BrooklynApi(SeaCloudsProperties.get(SeaCloudsProperties.DEPLOYER_ENDPOINT));

    @GET
    @Produces("text/plain")
    public String listApplications() {
        List<ApplicationSummary> applicationSummaries = BROOKLKYN_API.getApplicationApi().list();
        Response response;

        Collections.sort(applicationSummaries, new Comparator<ApplicationSummary>() {
            @Override
            public int compare(ApplicationSummary s1, ApplicationSummary s2) {
                return s1.getId().compareTo(s2.getId());
            }
        });


        if (applicationSummaries == null) {
            response.sendError(500, "Connection error: couldn't reach SeaClouds endpoint");
        } else {

            JsonArray jsonResult = new JsonArray();

            for (ApplicationSummary application : applicationSummaries) {

                JsonObject jsonApplication = new JsonObject();
                jsonResult.add(jsonApplication);

                jsonApplication.addProperty("id", application.getId());
                jsonApplication.addProperty("status", application.getStatus().toString());

                JsonObject jsonSpec = new JsonObject();
                jsonApplication.add("spec", jsonSpec);

                jsonSpec.addProperty("name", application.getSpec().getName());
                jsonSpec.addProperty("type", application.getSpec().getName());

                JsonArray jsonDescendantsEntities = new JsonArray();
                jsonApplication.add("descendants", jsonDescendantsEntities);

                List<EntitySummary> descendants;
                try {
                    descendants = BROOKLKYN_API.getEntityApi().list(application.getId());

                    if (descendants != null) {
                        for (EntitySummary childEntity : descendants) {
                            JsonObject jsonDescendantEntity = new JsonObject();
                            jsonDescendantsEntities.add(jsonDescendantEntity);


                            jsonDescendantEntity.addProperty("id", childEntity.getId());
                            jsonDescendantEntity.addProperty("name", childEntity.getName());
                            jsonDescendantEntity.addProperty("type", childEntity.getType());

                            JsonArray jsonArrayLocations = new JsonArray();
                            jsonDescendantEntity.add("locations", jsonArrayLocations);

                            List<LocationSummary> locations = BROOKLKYN_API.getEntityApi().getLocations(application.getId(), childEntity.getId());
                            if (locations != null) {

                                for (LocationSummary locationSummary : locations) {
                                    LocationSummary locationDetails = BROOKLKYN_API.getLocationApi().get(locationSummary.getId(), null);

                                    if (locationDetails != null) {
                                        JsonObject jsonLocation = new JsonObject();
                                        jsonArrayLocations.add(jsonLocation);
                                        jsonLocation.addProperty("id", locationDetails.getId());
                                        jsonLocation.addProperty("name", locationDetails.getName());
                                        jsonLocation.addProperty("type", locationDetails.getType());
                                        jsonLocation.addProperty("spec", locationDetails.getSpec());
                                    }
                                }
                            }
                        }
                    }
                } catch (ClientResponseFailure e) {
                    // The application removed after calling getApplicationApi().list()
                    jsonResult.remove(jsonApplication);
                }
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(jsonResult));
        }
    }
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://localhost:9998/");
        server.start();

        System.out.println("Server running");
        System.out.println("Visit: http://localhost:9998/helloworld");
        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");
    }
}
