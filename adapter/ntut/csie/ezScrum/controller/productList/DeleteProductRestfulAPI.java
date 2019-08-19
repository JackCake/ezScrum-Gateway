package ntut.csie.ezScrum.controller.productList;

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

@Path("/products")
@Singleton
public class DeleteProductRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();

	@DELETE
	@Path("/{product_id}")
	public synchronized String deleteProduct(@PathParam("product_id") String productId) {
		JSONObject deleteProductOutput = new JSONObject();
		try {
			deleteReleasesByProductId(productId);
			deleteSprintsByProductId(productId);
			deleteBacklogItemImportancesAndTasksByProductId(productId);
			
			Response response = productDelegator.deleteProduct(productId);
			boolean deleteSuccess = response.getStatus() == Response.Status.OK.getStatusCode();
			deleteProductOutput.put("deleteSuccess", deleteSuccess);
			if(deleteSuccess) {
				deleteProductOutput.put("errorMessage", "");
			} else {
				deleteProductOutput.put("errorMessage", "Sorry, please try again!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Map<String, Object> deleteProductOutputMap = new HashMap<>();
			deleteProductOutputMap.put("deleteSuccess", false);
			deleteProductOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject deleteProductOutputJSON = new JSONObject(deleteProductOutputMap);
			return deleteProductOutputJSON.toString();
		}
		return deleteProductOutput.toString();
	}
	
	private void deleteReleasesByProductId(String productId) throws JSONException {
		JSONArray releasesJSON = releaseDelegator.getReleasesByProductId(productId);
		for(int i = 0; i < releasesJSON.length(); i++) {
			JSONObject releaseJSON = releasesJSON.getJSONObject(i);
			String releaseId = releaseJSON.getString("releaseId");
			releaseDelegator.deleteRelease(releaseId);
		}
	}
	
	private void deleteSprintsByProductId(String productId) throws JSONException {
		JSONArray sprintsJSON = sprintDelegator.getSprintsByProductId(productId);
		for(int i = 0; i < sprintsJSON.length(); i ++) {
			JSONObject sprintJSON = sprintsJSON.getJSONObject(i);
			String sprintId = sprintJSON.getString("sprintId");
			sprintDelegator.deleteSprint(sprintId);
		}
	}
	
	private void deleteBacklogItemImportancesAndTasksByProductId(String productId) throws JSONException {
		JSONArray stagesJSON = productDelegator.getStagesInBoardByProductId(productId);
		for(int i = 0; i < stagesJSON.length(); i++) {
			JSONObject stageJSON = stagesJSON.getJSONObject(i);
			JSONObject miniStageJSON = stageJSON.getJSONArray("miniStageList").getJSONObject(0);
			JSONObject swimLaneJSON = miniStageJSON.getJSONArray("swimLaneList").getJSONObject(0);
			String swimLaneId = swimLaneJSON.getString("swimLaneId");
			JSONArray workItemsJSON = backlogItemDelegator.getWorkItemsBySwimLaneId(swimLaneId);
			for(int j = 0; j < workItemsJSON.length(); j++) {
				JSONObject workItemJSON = workItemsJSON.getJSONObject(j);
				String workItemId = workItemJSON.getString("workItemId");
				JSONArray tasksJSON = taskDelegator.getTasksByWorkItemId(workItemId);
				for(int k = 0; k < tasksJSON.length(); k++) {
					JSONObject taskJSON = tasksJSON.getJSONObject(k);
					String taskId = taskJSON.getString("taskId");
					taskDelegator.deleteTask(taskId);
				}
				backlogItemImportanceDelegator.deleteBacklogItemImportance(workItemId);
			}
		}
	}
}
