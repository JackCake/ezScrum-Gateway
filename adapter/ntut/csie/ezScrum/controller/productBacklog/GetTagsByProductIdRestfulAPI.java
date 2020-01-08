package ntut.csie.ezScrum.controller.productBacklog;

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
import ntut.csie.ezScrum.controller.delegator.TagDelegator;

@Path("/products/{product_id}/tags")
@Singleton
public class GetTagsByProductIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TagDelegator tagDelegator = applicationContext.newTagDelegator();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getTagsByProductId(@PathParam("product_id") String productId) {
		JSONObject getTagsByProductIdOutput = new JSONObject();
		try {
			JSONArray tagList = tagDelegator.getTagsByProductId(productId);
			
			getTagsByProductIdOutput.put("tagList", tagList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getTagsByProductIdOutput.toString();
	}
}
