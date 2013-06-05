package org.jboss.qa.perfrepo.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.qa.perfrepo.dao.ValueParameterDAO;
import org.jboss.qa.perfrepo.model.ValueParameter;

@Path("/valueParameter")
@RequestScoped
public class ValueParameterREST {

	@Inject
	private ValueParameterDAO dao;

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/getAll")
	public List<ValueParameter> findAll() {
		return dao.findAll();
	}

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/get/{valueParameterId}")
	public ValueParameter findById(@PathParam("valueParameterId") Long valueParameterId) {
		return dao.get(valueParameterId);
	}
	
	@POST()
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   public Long create(ValueParameter valueParameter) {
      dao.create(valueParameter);
      return valueParameter.getId();
   }
	
//	@GET
//@Produces("text/xml")
//@Path("/getByValue/{valueId}")
//public List<ValueParameter> findByValue(@PathParam("valueId") Integer valueId) {
//return dao.findByValue(valueId);
//}

}