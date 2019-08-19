package ntut.csie.ezScrum.controller.productBacklog;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.BacklogItemDelegator;
import ntut.csie.ezScrum.controller.delegator.BacklogItemImportanceDelegator;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;
import ntut.csie.ezScrum.controller.delegator.TaskDelegator;

@Path("/products/{product_id}/backlog_items")
@Singleton
public class DeleteBacklogItemRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();
	
	@DELETE
	@Path("/{backlog_item_id}")
	public synchronized String deleteBacklogItem(
			@PathParam("product_id") String productId, 
			@PathParam("backlog_item_id") String backlogItemId) {
		JSONObject deleteBacklogItemOutput = new JSONObject();
		try {
			JSONArray releasesJSON = releaseDelegator.getReleasesByProductId(productId);
			for(int i = 0; i < releasesJSON.length(); i++) {
				JSONObject releaseJSON = releasesJSON.getJSONObject(i);
				String releaseId = releaseJSON.getString("releaseId");
				releaseDelegator.unscheduleBacklogItemFromRelease(backlogItemId, releaseId);
			}
			
			JSONArray sprintsJSON = sprintDelegator.getSprintsByProductId(productId);
			for(int i = 0; i < sprintsJSON.length(); i ++) {
				JSONObject sprintJSON = sprintsJSON.getJSONObject(i);
				String sprintId = sprintJSON.getString("sprintId");
				sprintDelegator.dropBacklogItemFromSprint(backlogItemId, sprintId);
			}
			
			deleteTasksByWorkItemId(backlogItemId);
			
			JSONObject idsJSON = getIdsJSON(productId, backlogItemId);
			String swimLaneId = idsJSON.getString("swimLaneId");
			String miniStageId = idsJSON.getString("miniStageId");
			String stageId = idsJSON.getString("stageId");
			
			Response response = backlogItemDelegator.deleteBacklogItem(backlogItemId, swimLaneId, miniStageId, stageId);
			response = backlogItemImportanceDelegator.deleteBacklogItemImportance(backlogItemId);
			boolean deleteSuccess = response.getStatus() == Response.Status.OK.getStatusCode();
			deleteBacklogItemOutput.put("deleteSuccess", deleteSuccess);
			if(deleteSuccess) {
				deleteBacklogItemOutput.put("errorMessage", "");
			} else {
				deleteBacklogItemOutput.put("errorMessage", "Sorry, please try again!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> deleteBacklogItemOutputMap = new HashMap<>();
			deleteBacklogItemOutputMap.put("deleteSuccess", false);
			deleteBacklogItemOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject deleteBacklogItemOutputJSON = new JSONObject(deleteBacklogItemOutputMap);
			return deleteBacklogItemOutputJSON.toString();
		}
		return deleteBacklogItemOutput.toString();
	}
	
	private void deleteTasksByWorkItemId(String workItemId) throws JSONException {
		JSONArray tasksJSON = taskDelegator.getTasksByWorkItemId(workItemId);
		for(int i = 0; i < tasksJSON.length(); i++) {
			taskDelegator.deleteTask(tasksJSON.getJSONObject(i).getString("taskId"));
		}
	}

	private JSONObject getIdsJSON(String productId, String backlogItemId) throws JSONException {
		JSONArray stagesJSON = productDelegator.getStagesInBoardByProductId(productId);
		for(int i = 0; i < 3; i++) {
			JSONObject stageJSON = stagesJSON.getJSONObject(i);
			String stageId = stageJSON.getString("stageId");
			JSONObject miniStageJSON = stageJSON.getJSONArray("miniStageList").getJSONObject(0);
			String miniStageId = miniStageJSON.getString("miniStageId");
			JSONObject swimLaneJSON = miniStageJSON.getJSONArray("swimLaneList").getJSONObject(0);
			String swimLaneId = swimLaneJSON.getString("swimLaneId");
			JSONArray workItemsJSON = backlogItemDelegator.getWorkItemsBySwimLaneId(swimLaneId);
			for(int j = 0; j < workItemsJSON.length(); j++) {
				JSONObject workItemJSON = workItemsJSON.getJSONObject(j);
				String workItemId = workItemJSON.getString("workItemId");
				if(backlogItemId.equals(workItemId)) {
					JSONObject idsJSON = new JSONObject();
					idsJSON.put("swimLaneId", swimLaneId);
					idsJSON.put("miniStageId", miniStageId);
					idsJSON.put("stageId", stageId);
					return idsJSON;
				}
			}
		}
		return null;
	}
}
