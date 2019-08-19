package ntut.csie.ezScrum.controller.releasePlan;

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
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;

@Path("/releases/{release_id}/scheduled_backlog_items")
@Singleton
public class ScheduleBacklogItemToReleaseRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String scheduleBacklogItemToRelease(
			@PathParam("release_id") String releaseId, 
			String scheduledBacklogItemInfo) {
		String responseString = "";
		try {
			JSONObject scheduledBacklogItemJSON = new JSONObject(scheduledBacklogItemInfo);
			String backlogItemIds = scheduledBacklogItemJSON.getString("backlogItemIds");
			JSONArray backlogItemIdsJSON = new JSONArray(backlogItemIds);
			for(int i = 0; i < backlogItemIdsJSON.length(); i++) {
				String backlogItemId = backlogItemIdsJSON.getString(i);
				Response response = releaseDelegator.scheduleBacklogItemToRelease(backlogItemId, releaseId);
				responseString = response.readEntity(String.class);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> scheduleBacklogItemOutputMap = new HashMap<>();
			scheduleBacklogItemOutputMap.put("scheduleSuccess", false);
			scheduleBacklogItemOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject scheduleBacklogItemOutputJSON = new JSONObject(scheduleBacklogItemOutputMap);
			return scheduleBacklogItemOutputJSON.toString();
		}
		return responseString;
	}
}
