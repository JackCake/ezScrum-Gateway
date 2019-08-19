package ntut.csie.ezScrum.controller.releasePlan;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;

@Path("/releases/{release_id}/scheduled_backlog_items")
@Singleton
public class UnscheduleBacklogItemFromReleaseRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();
	
	@DELETE
	@Path("/{backlog_item_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String dropBacklogItem(
			@PathParam("release_id") String releaseId, 
			@PathParam("backlog_item_id") String backlogItemId) {
		Response response = releaseDelegator.unscheduleBacklogItemFromRelease(backlogItemId, releaseId);
		return response.readEntity(String.class);
	}
}
