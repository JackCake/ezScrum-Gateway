package ntut.csie.ezScrum.controller.productBacklog;

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
import ntut.csie.ezScrum.controller.delegator.TagDelegator;

@Path("/products/{product_id}/tags")
@Singleton
public class AddTagRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TagDelegator tagDelegator = applicationContext.newTagDelegator();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String addTag(
			@PathParam("product_id") String productId, 
			String tagInfo) {
		String responseString = "";
		try {
			JSONObject tagJSON = new JSONObject(tagInfo);
			String name = tagJSON.getString("name");
			
			Response response = tagDelegator.addTag(name, productId);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> addTagOutputMap = new HashMap<>();
			addTagOutputMap.put("addSuccess", false);
			addTagOutputMap.put("errorMessage", "Sorry, there is the problem when add the tag. Please contact to the system administrator!");
			JSONObject addTagOutputJSON = new JSONObject(addTagOutputMap);
			return addTagOutputJSON.toString();
		}
		return responseString;
	}
}
	
