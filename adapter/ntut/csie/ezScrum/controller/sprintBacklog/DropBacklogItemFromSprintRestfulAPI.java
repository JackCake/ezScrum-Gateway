package ntut.csie.ezScrum.controller.sprintBacklog;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;

@Path("/sprints/{sprint_id}/committed_backlog_items")
@Singleton
public class DropBacklogItemFromSprintRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	
	@DELETE
	@Path("/{backlog_item_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String dropBacklogItem(
			@PathParam("sprint_id") String sprintId, 
			@PathParam("backlog_item_id") String backlogItemId) {
		Response response = sprintDelegator.dropBacklogItemFromSprint(backlogItemId, sprintId);
		return response.readEntity(String.class);
	}
}
