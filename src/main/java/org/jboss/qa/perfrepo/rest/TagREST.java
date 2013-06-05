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

import org.jboss.qa.perfrepo.dao.TagDAO;
import org.jboss.qa.perfrepo.model.Tag;

@Path("/tag")
@RequestScoped
public class TagREST {

	@Inject
	private TagDAO dao;

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/getAll")
	public List<Tag> findAll() {
		return dao.findAll();
	}

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/get/{tagId}")
	public Tag findById(@PathParam("tagId") Long tagId) {
		return dao.get(tagId);
	}
	
	@POST()
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   public Long create(Tag tag) {
      dao.create(tag);
      return tag.getId();
   }
	
	
}