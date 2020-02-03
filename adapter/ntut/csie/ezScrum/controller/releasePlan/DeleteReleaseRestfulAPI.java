package ntut.csie.ezScrum.controller.releasePlan;

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
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;

@Path("/releases")
@Singleton
public class DeleteReleaseRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();

	@DELETE
	@Path("/{release_id}")
	public synchronized String deletRelease(@PathParam("release_id") String releaseId) {
		String responseString = "";
		try {
			Response response = releaseDelegator.deleteRelease(releaseId);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> deleteReleaseOutputMap = new HashMap<>();
			deleteReleaseOutputMap.put("deleteSuccess", false);
			deleteReleaseOutputMap.put("errorMessage", "Sorry, there is the problem when delete the release. Please contact to the system administrator!");
			JSONObject deleteReleaseOutputJSON = new JSONObject(deleteReleaseOutputMap);
			return deleteReleaseOutputJSON.toString();
		}
		return responseString;
	}
}
