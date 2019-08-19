package ntut.csie.ezScrum.controller.retrospective;

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

@Path("/products/{productId}/retrospectives")
@Singleton
public class GetRetrospectivesByProductIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getRetrospectivesByProductId(@PathParam("productId") String productId) {
		JSONObject getRetrospectivesByProductIdOutput = new JSONObject();
		try {
			JSONArray retrospectiveList = new JSONArray();
			JSONArray sprintsJSON = sprintDelegator.getSprintsByProductId(productId);
			for(int i = 0; i < sprintsJSON.length(); i++) {
				JSONObject retrospectiveJSON = new JSONObject();
				JSONObject sprintJSON = sprintsJSON.getJSONObject(i);
				retrospectiveJSON.put("sprintId", sprintJSON.getString("sprintId"));
				retrospectiveJSON.put("sprintOrderId", sprintJSON.getInt("orderId"));
				retrospectiveJSON.put("retrospectiveDiscussion", sprintJSON.getString("retrospective"));
				retrospectiveList.put(retrospectiveJSON);
			}
			
			getRetrospectivesByProductIdOutput.put("retrospectiveList", retrospectiveList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getRetrospectivesByProductIdOutput.toString();
	}
}
