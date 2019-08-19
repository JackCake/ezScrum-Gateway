package ntut.csie.ezScrum.controller.sprintBacklog;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.BacklogItemDelegator;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;

@Path("products/{product_id}/backlog_item_statuses")
@Singleton
public class MoveBacklogItemRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	
	@PUT
	@Path("/{backlog_item_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String moveBacklogItem(
			@PathParam("product_id") String productId, 
			@PathParam("backlog_item_id") String backlogItemId, 
			String backlogItemInfo) {
		JSONObject moveBacklogItemOutput = new JSONObject();
		try {
			JSONArray stagesJSON = productDelegator.getStagesInBoardByProductId(productId);
			Map<String, JSONObject> stageMap = new HashMap<>();
			Map<String, JSONObject> workItemMap = new HashMap<>();
			for(int i = 0; i < 3; i++) {
				JSONObject stageJSON = stagesJSON.getJSONObject(i);
				String stageId = stageJSON.getString("stageId");
				String stageTitle = stageJSON.getString("title");
				JSONObject miniStageJSON = stageJSON.getJSONArray("miniStageList").getJSONObject(0);
				JSONObject swimLaneJSON = miniStageJSON.getJSONArray("swimLaneList").getJSONObject(0);
				String swimLaneId = swimLaneJSON.getString("swimLaneId");
				JSONObject stageJSONWithSwimLaneId = new JSONObject();
				stageJSONWithSwimLaneId.put("stageId", stageId);
				stageJSONWithSwimLaneId.put("swimLaneId", swimLaneId);
				stageMap.put(stageTitle, stageJSONWithSwimLaneId);
				JSONArray workItemsJSON = backlogItemDelegator.getWorkItemsBySwimLaneId(swimLaneId);
				for(int j = 0; j < workItemsJSON.length(); j++) {
					JSONObject workItemJSON = workItemsJSON.getJSONObject(j);
					String workItemId = workItemJSON.getString("workItemId");
					JSONObject workItemJSONWithStageIdAndSwimLaneId = new JSONObject();
					workItemJSONWithStageIdAndSwimLaneId.put("backlogItemId", workItemId);
					workItemJSONWithStageIdAndSwimLaneId.put("originalStageId", stageId);
					workItemJSONWithStageIdAndSwimLaneId.put("originalSwimLaneId", swimLaneId);
					workItemMap.put(workItemId, workItemJSONWithStageIdAndSwimLaneId);
				}
			}
			
			JSONObject backlogItemJSON = new JSONObject(backlogItemInfo);
			String status = backlogItemJSON.getString("status");
			
			JSONObject movingBacklogItemJSON = workItemMap.get(backlogItemId);
			if(movingBacklogItemJSON == null) {
				moveBacklogItemOutput.put("moveSuccess", false);
				moveBacklogItemOutput.put("errorMessage", "Sorry, the backlog item is not exist.");
				return moveBacklogItemOutput.toString();
			}
			String originalStageId = movingBacklogItemJSON.getString("originalStageId");
			String originalSwimLaneId = movingBacklogItemJSON.getString("originalSwimLaneId");
			JSONObject stageJSONWithSwimLaneId = stageMap.get(status);
			String newStageId = stageJSONWithSwimLaneId.getString("stageId");
			String newSwimLaneId = stageJSONWithSwimLaneId.getString("swimLaneId");
			Response response = backlogItemDelegator.moveBacklogItem(productId, originalStageId, newStageId, originalSwimLaneId, newSwimLaneId, backlogItemId);
			boolean moveSuccess = response.getStatus() == Response.Status.OK.getStatusCode();
			moveBacklogItemOutput.put("moveSuccess", moveSuccess);
			if(moveSuccess) {
				moveBacklogItemOutput.put("errorMessage", "");
			} else {
				moveBacklogItemOutput.put("errorMessage", "Sorry, please try again!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> moveBacklogItemOutputMap = new HashMap<>();
			moveBacklogItemOutputMap.put("moveSuccess", false);
			moveBacklogItemOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject moveBacklogItemOutputJSON = new JSONObject(moveBacklogItemOutputMap);
			return moveBacklogItemOutputJSON.toString();
		}
		return moveBacklogItemOutput.toString();
	}
}
