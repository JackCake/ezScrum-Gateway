package ntut.csie.ezScrum.controller.productBacklog;

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
import ntut.csie.ezScrum.controller.delegator.TagDelegator;

@Path("/tags")
@Singleton
public class EditTagRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TagDelegator tagDelegator = applicationContext.newTagDelegator();

	@PUT
	@Path("/{tag_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String editTag(
			@PathParam("tag_id") String tagId, 
			String tagInfo) {
		String responseString = "";
		try {
			JSONObject tagJSON = new JSONObject(tagInfo);
			String name = tagJSON.getString("name");
			
			Response response = tagDelegator.editTag(tagId, name);
			responseString = response.readEntity(String.class);
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> editTagOutputMap = new HashMap<>();
			editTagOutputMap.put("editSuccess", false);
			editTagOutputMap.put("errorMessage", "Sorry, there is the problem when edit the tag. Please contact to the system administrator!");
			JSONObject editTagOutputJSON = new JSONObject(editTagOutputMap);
			return editTagOutputJSON.toString();
		}
		return responseString;
	}
}
