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

import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;

@Path("/products/{product_id}/releases")
@Singleton
public class AddReleaseRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String addRelease(
			@PathParam("product_id") String productId, 
			String releaseInfo) {
		String responseString = "";
		try {
			JSONObject sprintJSON = new JSONObject(releaseInfo);
			String name = sprintJSON.getString("name");
			String startDate = sprintJSON.getString("startDate");
			String endDate = sprintJSON.getString("endDate");
			String description = sprintJSON.getString("description");
			
			Response response = releaseDelegator.addRelease(name, startDate, endDate, description, productId);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> addReleaseOutputMap = new HashMap<>();
			addReleaseOutputMap.put("addSuccess", false);
			addReleaseOutputMap.put("errorMessage", "Sorry, there is the problem when add the release. Please contact to the system administrator!");
			JSONObject addReleaseOutputJSON = new JSONObject(addReleaseOutputMap);
			return addReleaseOutputJSON.toString();
		}
		return responseString;
	}
}
