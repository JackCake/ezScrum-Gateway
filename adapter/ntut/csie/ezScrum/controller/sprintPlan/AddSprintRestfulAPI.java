package ntut.csie.ezScrum.controller.sprintPlan;

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

import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;

@Path("/products/{product_id}/sprints")
@Singleton
public class AddSprintRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String addSprint(
			@PathParam("product_id") String productId, 
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
			
			Response response = sprintDelegator.addSprint(goal, interval, startDate, endDate, demoDate, demoPlace, daily, productId);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> addSprintOutputMap = new HashMap<>();
			addSprintOutputMap.put("addSuccess", false);
			addSprintOutputMap.put("errorMessage", "Sorry, there is the problem when add the sprint. Please contact to the system administrator!");
			JSONObject addSprintOutputJSON = new JSONObject(addSprintOutputMap);
			return addSprintOutputJSON.toString();
		}
		return responseString;
	}
}
