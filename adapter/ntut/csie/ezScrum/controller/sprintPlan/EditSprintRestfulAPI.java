package ntut.csie.ezScrum.controller.sprintPlan;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;

@Path("/sprints")
@Singleton
public class EditSprintRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();

	@PUT
	@Path("/{sprint_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String editSprint(
			@PathParam("sprint_id") String sprintId,
			String sprintInfo) {
		String responseString = "";
		try {
			JSONObject sprintJSON = new JSONObject(sprintInfo);
			String goal = sprintJSON.getString("goal");
			int interval = sprintJSON.getInt("interval");
			String startDate = sprintJSON.getString("startDate");
			String endDate = sprintJSON.getString("endDate");
			String demoDate = sprintJSON.getString("demoDate");
			String demoPlace = sprintJSON.getString("demoPlace");
			String daily = sprintJSON.getString("daily");
			
			Response response = sprintDelegator.editSprint(sprintId, goal, interval, startDate, endDate, demoDate, demoPlace, daily);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> editSprintOutputMap = new HashMap<>();
			editSprintOutputMap.put("editSuccess", false);
			editSprintOutputMap.put("errorMessage", "Sorry, there is the problem when edit the sprint. Please contact to the system administrator!");
			JSONObject editSprintOutputJSON = new JSONObject(editSprintOutputMap);
			return editSprintOutputJSON.toString();
		}
		return responseString;
	}
}
