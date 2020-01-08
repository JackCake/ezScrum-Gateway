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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.TagDelegator;

@Path("backlog_items/{backlog_item_id}/assigned_tags")
@Singleton
public class AssignTagToBacklogItemRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TagDelegator tagDelegator = applicationContext.newTagDelegator();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String assignTagToBacklogItem(
			@PathParam("backlog_item_id") String backlogItemId, 
			String assignedTagInfo) {
		String responseString = "";
		try {
			JSONObject assignedTagJSON = new JSONObject(assignedTagInfo);
			String tagIds = assignedTagJSON.getString("tagIds");
			JSONArray tagIdsJSON = new JSONArray(tagIds);
			for(int i = 0; i < tagIdsJSON.length(); i++) {
				String tagId = tagIdsJSON.getString(i);
				Response response = tagDelegator.assignTagToBacklogItem(backlogItemId, tagId);
				responseString = response.readEntity(String.class);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> assignTagOutputMap = new HashMap<>();
			assignTagOutputMap.put("assignSuccess", false);
			assignTagOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject assignTagOutputJSON = new JSONObject(assignTagOutputMap);
			return assignTagOutputJSON.toString();
		}
		return responseString;
	}
}
