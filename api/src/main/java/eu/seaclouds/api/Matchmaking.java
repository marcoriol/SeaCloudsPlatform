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

import com.google.gson.Gson;
import eu.seaclouds.planner.Planner;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/matchmaking")
public class Matchmaking {

    @GET
    @Produces("application/json")
    public Response getMatchmaking(@QueryParam("yaml") String yaml) {

        if (yaml == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing argument: yaml")
                    .build();
        } else {
            try {
                Planner planner = new Planner(yaml);
                return Response.ok().entity(new Gson().toJson(planner.plan())).build();
            } catch (Exception e){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Internal server error")
                        .build();
            }
        }
    }

}
