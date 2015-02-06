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
package eu.seaclouds.api;

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.EntitySummary;
import brooklyn.rest.domain.SensorSummary;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.seaclouds.core.SeaCloudsProperties;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Path("/metrics")
public class Metrics {

    final static BrooklynApi BROOKLKYN_API = new BrooklynApi(SeaCloudsProperties.get(SeaCloudsProperties.DEPLOYER_ENDPOINT));

    @GET
    @Produces("application/json")
    public Response getAvailableMetrics(@QueryParam("application") String application) {
        if(application != null){

            JsonArray parentJson = new JsonArray();

            for(EntitySummary entitySummary : BROOKLKYN_API.getEntityApi().list(application)) {
                JsonObject entitySumaryJson = new JsonObject();
                entitySumaryJson.addProperty("id", entitySummary.getId());
                entitySumaryJson.addProperty("name", entitySummary.getName());
                entitySumaryJson.addProperty("type", entitySummary.getType());

                JsonArray entityMetricsJsonArray = new JsonArray();
                entitySumaryJson.add("metrics", entityMetricsJsonArray);

                List<SensorSummary> sensorSummaryList = BROOKLKYN_API.getSensorApi().list(application, entitySummary.getId());
                Collections.sort(sensorSummaryList, new Comparator<SensorSummary>() {
                    @Override
                    public int compare(SensorSummary s1, SensorSummary s2) {
                        return s1.getName().compareTo(s2.getName());
                    }
                });

                for (SensorSummary sensorSummary : sensorSummaryList) {
                    if(isNumberType(sensorSummary)) {
                        entityMetricsJsonArray.add(new Gson().toJsonTree(sensorSummary));
                    }
                }
                parentJson.add(entitySumaryJson);
            }

            return Response.ok().entity(new Gson().toJson(parentJson.toString())).build();
        }else{
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing argument: application")
                    .build();
        }

    }

    @GET
    @Produces("text/plain")
    public Response getMetricValue(
            @QueryParam("application") String application,
            @QueryParam("entity") String entity,
            @QueryParam("sensor") String sensor
    ) {

        Object sensorValue = BROOKLKYN_API.getSensorApi().get(application, entity, sensor, true);

        if (sensorValue == null){
            return Response.status(Response
                    .Status.INTERNAL_SERVER_ERROR)
                    .entity("Connection error: couldn't reach SeaClouds endpoint")
                    .build();
        }else{
            return Response.ok().entity(new Gson().toJson(sensorValue)).build();
        }
    }

    private boolean isNumberType(SensorSummary sensor){
        return sensor.getType().equals("java.lang.Integer")
                || sensor.getType().equals("java.lang.Double")
                || sensor.getType().equals("java.lang.Float")
                || sensor.getType().equals("java.lang.Long")
                || sensor.getType().equals("java.lang.Short")
                || sensor.getType().equals("java.lang.BigDecimal")
                || sensor.getType().equals("java.lang.BigInteger")
                || sensor.getType().equals("java.lang.Byte");
    }

}
