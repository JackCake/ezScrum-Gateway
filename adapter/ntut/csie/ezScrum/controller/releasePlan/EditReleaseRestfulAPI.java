package ntut.csie.ezScrum.controller.releasePlan;

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
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;

@Path("/releases")
@Singleton
public class EditReleaseRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();

	@PUT
	@Path("/{release_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String editRelease(
			@PathParam("release_id") String releaseId, 
			String releaseInfo) {
		String responseString = "";
		try {
			JSONObject releaseJSON = new JSONObject(releaseInfo);
			String name = releaseJSON.getString("name");
			String startDate = releaseJSON.getString("startDate");
			String endDate = releaseJSON.getString("endDate");
			String description = releaseJSON.getString("description");
			
			Response response = releaseDelegator.editRelease(releaseId, name, startDate, endDate, description);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> editReleaseOutputMap = new HashMap<>();
			editReleaseOutputMap.put("editSuccess", false);
			editReleaseOutputMap.put("errorMessage", "Sorry, there is the problem when edit the release. Please contact to the system administrator!");
			JSONObject editReleaseOutputJSON = new JSONObject(editReleaseOutputMap);
			return editReleaseOutputJSON.toString();
		}
		return responseString;
	}
}
