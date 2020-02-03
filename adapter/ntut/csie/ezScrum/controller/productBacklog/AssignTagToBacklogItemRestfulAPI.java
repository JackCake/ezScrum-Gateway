package ntut.csie.ezScrum.controller.productBacklog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
			Map<String, String> originalAssignedTagMap = new HashMap<>();
			JSONArray assignedTagsJSON = tagDelegator.getAssignedTagsByBacklogItemId(backlogItemId);
			for(int i = 0; i < assignedTagsJSON.length(); i++) {
				JSONObject assignedTagJSON = assignedTagsJSON.getJSONObject(i);
				String assignedTagId = assignedTagJSON.getString("assignedTagId");
				String tagId = assignedTagJSON.getString("tagId");
				originalAssignedTagMap.put(tagId, assignedTagId);
			}
			
			Set<String> newTagIds = new HashSet<>();
			JSONObject assignedTagJSON = new JSONObject(assignedTagInfo);
			String tagIds = assignedTagJSON.getString("tagIds");
			JSONArray tagIdsJSON = new JSONArray(tagIds);
			for(int i = 0; i < tagIdsJSON.length(); i++) {
				String tagId = tagIdsJSON.getString(i);
				newTagIds.add(tagId);
				if(!originalAssignedTagMap.containsKey(tagId)) {
					Response response = tagDelegator.assignTagToBacklogItem(backlogItemId, tagId);
					responseString = response.readEntity(String.class);
					JSONObject assignTagOutputJSON = new JSONObject(responseString);
					boolean assignSuccess = assignTagOutputJSON.getBoolean("assignSuccess");
					if(!assignSuccess) {
						return responseString;
					}
				}
			}
			
			for(String tagId : originalAssignedTagMap.keySet()) {
				if(!newTagIds.contains(tagId)) {
					String assignedTagId = originalAssignedTagMap.get(tagId);
					Response response = tagDelegator.unassignTagFromBacklogItem(assignedTagId);
					responseString = response.readEntity(String.class);
					JSONObject unassignTagOutputJSON = new JSONObject(responseString);
					boolean unassignSuccess = unassignTagOutputJSON.getBoolean("unassignSuccess");
					if(!unassignSuccess) {
						Map<String, Object> assignTagOutputMap = new HashMap<>();
						assignTagOutputMap.put("assignSuccess", false);
						assignTagOutputMap.put("errorMessage", unassignTagOutputJSON.getString("errorMessage"));
						JSONObject assignTagOutputJSON = new JSONObject(assignTagOutputMap);
						return assignTagOutputJSON.toString();
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> assignTagOutputMap = new HashMap<>();
			assignTagOutputMap.put("assignSuccess", false);
			assignTagOutputMap.put("errorMessage", "Sorry, there is the problem when assign the tag to the backlog item. Please contact to the system administrator!");
			JSONObject assignTagOutputJSON = new JSONObject(assignTagOutputMap);
			return assignTagOutputJSON.toString();
		}
		return responseString;
	}
}
