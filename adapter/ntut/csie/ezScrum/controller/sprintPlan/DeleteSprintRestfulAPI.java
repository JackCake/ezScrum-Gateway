package ntut.csie.ezScrum.controller.sprintPlan;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;

@Path("/sprints")
@Singleton
public class DeleteSprintRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	
	@DELETE
	@Path("/{sprint_id}")
	public synchronized String deleteSprint(@PathParam("sprint_id") String sprintId) {
		String responseString = "";
		try {
			dropBacklogItemsBySprintId(sprintId);
			
			Response response = sprintDelegator.deleteSprint(sprintId);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> deleteSprintOutputMap = new HashMap<>();
			deleteSprintOutputMap.put("deleteSuccess", false);
			deleteSprintOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject deleteSprintOutputJSON = new JSONObject(deleteSprintOutputMap);
			return deleteSprintOutputJSON.toString();
		}
		return responseString;
	}
	
	private void dropBacklogItemsBySprintId(String sprintId) throws JSONException {
		JSONArray committedBacklogItemsJSON = sprintDelegator.getCommittedBacklogItemsBySprintId(sprintId);
		for(int i = 0; i < committedBacklogItemsJSON.length(); i++) {
			JSONObject committedBacklogItemJSON = committedBacklogItemsJSON.getJSONObject(i);
			String backlogItemId = committedBacklogItemJSON.getString("backlogItemId");
			sprintDelegator.dropBacklogItemFromSprint(backlogItemId, sprintId);
		}
	}
}
