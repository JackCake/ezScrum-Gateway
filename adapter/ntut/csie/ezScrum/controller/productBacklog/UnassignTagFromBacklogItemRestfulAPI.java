package ntut.csie.ezScrum.controller.productBacklog;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.TagDelegator;

@Path("/assigned_tags")
@Singleton
public class UnassignTagFromBacklogItemRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TagDelegator tagDelegator = applicationContext.newTagDelegator();
	
	@DELETE
	@Path("/{assigned_tag_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String unassignTagFromBacklogItem(@PathParam("assigned_tag_id") String assignedTagId) {
		Response response = tagDelegator.unassignTagFromBacklogItem(assignedTagId);
		return response.readEntity(String.class);
	}
}
