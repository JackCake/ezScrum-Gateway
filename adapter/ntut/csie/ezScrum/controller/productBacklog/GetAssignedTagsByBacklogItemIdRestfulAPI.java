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

@Path("/backlog_items/{backlog_item_id}/assigned_tags")
@Singleton
public class GetAssignedTagsByBacklogItemIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TagDelegator tagDelegator = applicationContext.newTagDelegator();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getTagsByProductId(@PathParam("backlog_item_id") String backlogItemId) {
		JSONObject getAssignedTagsByBacklogItemIdOutput = new JSONObject();
		try {
			JSONArray assignedTagList = tagDelegator.getAssignedTagByBacklogItemId(backlogItemId);
			
			getAssignedTagsByBacklogItemIdOutput.put("assignedTagList", assignedTagList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getAssignedTagsByBacklogItemIdOutput.toString();
	}
}
