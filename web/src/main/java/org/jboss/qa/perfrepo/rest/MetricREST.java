/* 
 * Copyright 2013 Red Hat, Inc.
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
package org.jboss.qa.perfrepo.rest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.rest.logging.Logged;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

@Path("/metric")
@RequestScoped
public class MetricREST {

   @Inject
   private TestService testService;

   @GET
   @Produces(MediaType.TEXT_XML)
   @Path("/{metricId}")
   @Logged
   public Response get(@PathParam("metricId") Long metricId) {
      return Response.ok(testService.getFullMetric(metricId)).build();
   }

   @GET
   @Produces(MediaType.TEXT_XML)
   @Path("/all")
   @Logged
   @Wrapped(element = "metrics")
   public Response all() {
      return Response.ok(new GenericEntity<List<Metric>>(new ArrayList<Metric>(testService.getAllFullMetrics())) {
      }).build();
   }
}