package ntut.csie.ezScrum.controller.sprintPlan;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

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
			Response response = sprintDelegator.deleteSprint(sprintId);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> deleteSprintOutputMap = new HashMap<>();
			deleteSprintOutputMap.put("deleteSuccess", false);
			deleteSprintOutputMap.put("errorMessage", "Sorry, there is the problem when delete the sprint. Please contact to the system administrator!");
			JSONObject deleteSprintOutputJSON = new JSONObject(deleteSprintOutputMap);
			return deleteSprintOutputJSON.toString();
		}
		return responseString;
	}
}
