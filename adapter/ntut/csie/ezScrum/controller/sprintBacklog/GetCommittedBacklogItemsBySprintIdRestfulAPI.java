package ntut.csie.ezScrum.controller.sprintBacklog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ntut.csie.ezScrum.controller.delegator.BacklogItemDelegator;
import ntut.csie.ezScrum.controller.delegator.BacklogItemImportanceDelegator;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;

@Path("/products/{product_id}/sprints/{sprint_id}/committed_backlog_items")
@Singleton
public class GetCommittedBacklogItemsBySprintIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getCommittedBacklogItemsBySprintId(
			@PathParam("product_id") String productId, 
			@PathParam("sprint_id") String sprintId) throws JSONException {
		JSONObject getCommittedBacklogItemsBySprintIdOutput = new JSONObject();
		try {
			JSONArray stagesJSON = productDelegator.getStagesInBoardByProductId(productId);
			Map<String, JSONObject> backlogItemMap = new HashMap<>();
			for(int i = 0; i < 3; i++) {
				JSONObject stageJSON = stagesJSON.getJSONObject(i);
				JSONObject miniStageJSON = stageJSON.getJSONArray("miniStageList").getJSONObject(0);
				JSONObject swimLaneJSON = miniStageJSON.getJSONArray("swimLaneList").getJSONObject(0);
				String swimLaneId = swimLaneJSON.getString("swimLaneId");
				JSONArray workItemsJSON = backlogItemDelegator.getWorkItemsBySwimLaneId(swimLaneId);
				for(int j = 0; j < workItemsJSON.length(); j++) {
					JSONObject workItemJSON = workItemsJSON.getJSONObject(j);
					String workItemId = workItemJSON.getString("workItemId");
					String status = "To do";
					if(i == 1) {
						status = "Doing";
					}else if (i == 2) {
						status = "Done";
					}
					
					JSONObject backlogItemJSON = new JSONObject();
					backlogItemJSON.put("backlogItemId", workItemJSON.getString("workItemId"));
					backlogItemJSON.put("description", workItemJSON.getString("description"));
					backlogItemJSON.put("status", status);
					backlogItemJSON.put("estimate", workItemJSON.getInt("estimate"));
					backlogItemJSON.put("importance", backlogItemImportanceDelegator.getBacklogItemImportanceByBacklogItemId(workItemId).getInt("importance"));
					backlogItemJSON.put("notes", workItemJSON.getString("notes"));
					backlogItemJSON.put("productId", productId);
					backlogItemMap.put(workItemId, backlogItemJSON);
				}
			}
			
			JSONArray committedBacklogItemsJSON = sprintDelegator.getCommittedBacklogItemsBySprintId(sprintId);
			List<JSONObject> committedBacklogItemList = new ArrayList<>();
			for(int i = 0 ; i < committedBacklogItemsJSON.length(); i++) {
				JSONObject committedBacklogItemJSON = committedBacklogItemsJSON.getJSONObject(i);
				String backlogItemId = committedBacklogItemJSON.getString("backlogItemId");
				committedBacklogItemList.add(backlogItemMap.get(backlogItemId));
			}
			
			Collections.sort(committedBacklogItemList, new Comparator<JSONObject>() {

				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					int importance1 = 0;
					int importance2 = 0;
					try {
						importance1 = o1.getInt("importance");
						importance2 = o2.getInt("importance");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return importance2 - importance1;
				}
				
			});
			
			int orderId = 0;
			for(JSONObject committedBacklogItemJSON : committedBacklogItemList) {
				committedBacklogItemJSON.put("orderId", ++orderId);
			}
			
			getCommittedBacklogItemsBySprintIdOutput.put("committedBacklogItemList", committedBacklogItemList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getCommittedBacklogItemsBySprintIdOutput.toString();
	}
}
