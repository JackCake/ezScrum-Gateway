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
import ntut.csie.ezScrum.controller.delegator.BacklogItemDelegator;
import ntut.csie.ezScrum.controller.delegator.BacklogItemImportanceDelegator;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;

@Path("/products/{product_id}/backlog_items")
@Singleton
public class AddBacklogItemRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String addBacklogItem(
			@PathParam("product_id") String productId, 
			String backlogItemInfo) {
		JSONObject addBacklogItemOutput = new JSONObject();
		try {
			JSONObject backlogItemJSON = new JSONObject(backlogItemInfo);
			String description = backlogItemJSON.getString("description");
			int estimate = backlogItemJSON.getInt("estimate");
			int importance = backlogItemJSON.getInt("importance");
			String notes = backlogItemJSON.getString("notes");
			
			JSONArray stagesJSON = productDelegator.getStagesInBoardByProductId(productId);
			JSONObject toDoStageJSON = stagesJSON.getJSONObject(0);
			String toDoStageId = toDoStageJSON.getString("stageId");
			JSONObject toDoMiniStageJSON = toDoStageJSON.getJSONArray("miniStageList").getJSONObject(0);
			String toDoMiniStageId = toDoMiniStageJSON.getString("miniStageId");
			JSONObject toDoSwimLaneJSON = toDoMiniStageJSON.getJSONArray("swimLaneList").getJSONObject(0);
			String toDoSwimLaneId = toDoSwimLaneJSON.getString("swimLaneId");
			
			Response response = backlogItemDelegator.addBacklogItem(description, estimate, notes, toDoSwimLaneId, toDoMiniStageId, toDoStageId);
			JSONArray backlogItemsJSON = backlogItemDelegator.getWorkItemsBySwimLaneId(toDoSwimLaneId);
			JSONObject lastBacklogItemJSON = backlogItemsJSON.getJSONObject(backlogItemsJSON.length() - 1);
			String backlogItemId = lastBacklogItemJSON.getString("workItemId");
			response = backlogItemImportanceDelegator.addBacklogItemImportance(backlogItemId, importance);
			
			boolean addSuccess = response.getStatus() == Response.Status.OK.getStatusCode();
			addBacklogItemOutput.put("addSuccess", addSuccess);
			if(addSuccess) {
				addBacklogItemOutput.put("errorMessage", "");
				addBacklogItemOutput.put("backlogItemId", backlogItemId);
			} else {
				addBacklogItemOutput.put("errorMessage", "Sorry, please try again!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> addBacklogItemOutputMap = new HashMap<>();
			addBacklogItemOutputMap.put("addSuccess", false);
			addBacklogItemOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject addBacklogItemOutputJSON = new JSONObject(addBacklogItemOutputMap);
			return addBacklogItemOutputJSON.toString();
		}
		return addBacklogItemOutput.toString();
	}
}
