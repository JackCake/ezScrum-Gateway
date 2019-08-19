package ntut.csie.ezScrum.controller.sprintPlan;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;

@Path("/products/{product_id}/sprints")
@Singleton
public class GetSprintsByProductIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getSprintsByProductId(@PathParam("product_id") String productId) {
		JSONObject getSprintsByProductIdOutput = new JSONObject();
		try {
			JSONArray sprintList = sprintDelegator.getSprintsByProductId(productId);
			
			getSprintsByProductIdOutput.put("sprintList", sprintList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getSprintsByProductIdOutput.toString();
	}
}
