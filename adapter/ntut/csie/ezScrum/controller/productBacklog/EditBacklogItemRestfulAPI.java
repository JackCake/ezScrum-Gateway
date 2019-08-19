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
import ntut.csie.ezScrum.controller.delegator.BacklogItemDelegator;
import ntut.csie.ezScrum.controller.delegator.BacklogItemImportanceDelegator;

@Path("/backlog_items")
@Singleton
public class EditBacklogItemRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	
	@PUT
	@Path("/{backlog_item_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String editBacklogItem(
			@PathParam("backlog_item_id") String backlogItemId, 
			String backlogItemInfo) {
		JSONObject editBacklogItemOutput = new JSONObject();
		try {
			JSONObject backlogItemJSON = new JSONObject(backlogItemInfo);
			String description = backlogItemJSON.getString("description");
			int estimate = backlogItemJSON.getInt("estimate");
			int importance = backlogItemJSON.getInt("importance");
			String notes = backlogItemJSON.getString("notes");
			
			Response response = backlogItemDelegator.editBacklogItem(backlogItemId, description, estimate, notes);
			response = backlogItemImportanceDelegator.editBacklogItemImportance(backlogItemId, importance);
			
			boolean editSuccess = response.getStatus() == Response.Status.OK.getStatusCode();
			editBacklogItemOutput.put("editSuccess", editSuccess);
			if(editSuccess) {
				editBacklogItemOutput.put("errorMessage", "");
			} else {
				editBacklogItemOutput.put("errorMessage", "Sorry, please try again!");
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> editBacklogItemOutputMap = new HashMap<>();
			editBacklogItemOutputMap.put("editSuccess", false);
			editBacklogItemOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject editBacklogItemOutputJSON = new JSONObject(editBacklogItemOutputMap);
			return editBacklogItemOutputJSON.toString();
		}
		return editBacklogItemOutput.toString();
	}
}
