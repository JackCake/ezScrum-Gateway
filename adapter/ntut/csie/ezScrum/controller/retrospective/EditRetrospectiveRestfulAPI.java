package ntut.csie.ezScrum.controller.retrospective;

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

@Path("/sprint_retrospectives")
@Singleton
public class EditRetrospectiveRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	
	@PUT
	@Path("/{sprint_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String editRetrospective(
			@PathParam("sprint_id") String sprintId, 
			String retrospectiveInfo) {
		String responseString = "";
		try {
			JSONObject retrospectiveJSON = new JSONObject(retrospectiveInfo);
			String retrospective = retrospectiveJSON.getString("retrospective");
			
			Response response = sprintDelegator.editRetrospective(sprintId, retrospective);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> editRetrospectiveOutputMap = new HashMap<>();
			editRetrospectiveOutputMap.put("editSuccess", false);
			editRetrospectiveOutputMap.put("errorMessage", "Sorry, there is the problem when edit the retrospective. Please contact to the system administrator!");
			JSONObject editRetrospectiveOutputJSON = new JSONObject(editRetrospectiveOutputMap);
			return editRetrospectiveOutputJSON.toString();
		}
		return responseString;
	}
}
