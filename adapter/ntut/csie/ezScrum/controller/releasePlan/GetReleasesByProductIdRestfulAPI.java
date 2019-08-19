package ntut.csie.ezScrum.controller.releasePlan;

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
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;

@Path("/products/{product_id}/releases")
@Singleton
public class GetReleasesByProductIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getReleasesByProductId(@PathParam("product_id") String productId) {
		JSONObject getReleasesByProductIdOutput = new JSONObject();
		try {
			JSONArray releaseList = releaseDelegator.getReleasesByProductId(productId);
			
			getReleasesByProductIdOutput.put("releaseList", releaseList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getReleasesByProductIdOutput.toString();
	}
}
