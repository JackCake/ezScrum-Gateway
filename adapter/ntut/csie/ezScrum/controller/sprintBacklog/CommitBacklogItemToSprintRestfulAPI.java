package ntut.csie.ezScrum.controller.sprintBacklog;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;

@Path("/sprints/{sprint_id}/committed_backlog_items")
@Singleton
public class CommitBacklogItemToSprintRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String commitBacklogItemToSprint(
			@PathParam("sprint_id") String sprintId, 
			String committedBacklogItemInfo) {
		String responseString = "";
		try {
			JSONObject committedBacklogItemJSON = new JSONObject(committedBacklogItemInfo);
			String backlogItemIds = committedBacklogItemJSON.getString("backlogItemIds");
			JSONArray backlogItemIdsJSON = new JSONArray(backlogItemIds);
			for(int i = 0; i < backlogItemIdsJSON.length(); i++) {
				String backlogItemId = backlogItemIdsJSON.getString(i);
				Response response = sprintDelegator.commitBacklogItemToSprint(backlogItemId, sprintId);
				responseString = response.readEntity(String.class);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> commitBacklogItemOutputMap = new HashMap<>();
			commitBacklogItemOutputMap.put("commitSuccess", false);
			commitBacklogItemOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject commitBacklogItemOutputJSON = new JSONObject(commitBacklogItemOutputMap);
			return commitBacklogItemOutputJSON.toString();
		}
		return responseString;
	}
}
