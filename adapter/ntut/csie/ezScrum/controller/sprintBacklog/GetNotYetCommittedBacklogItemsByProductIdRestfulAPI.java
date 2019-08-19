package ntut.csie.ezScrum.controller.sprintBacklog;

import java.util.HashSet;
import java.util.Set;

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

@Path("/products/{product_id}/not_yet_committed_backlog_items")
@Singleton
public class GetNotYetCommittedBacklogItemsByProductIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getNotYetCommittedBacklogItemsByProductId(@PathParam("product_id") String productId) {
		JSONObject getNotYetCommittedBacklogItemsByProductIdOutput = new JSONObject();
		try {
			JSONArray notYetCommittedBacklogItemList = new JSONArray();
			
			JSONArray stagesJSON = productDelegator.getStagesInBoardByProductId(productId);
			Set<String> committedBacklogItemIdSet = new HashSet<>();
			JSONArray sprintsJSON = sprintDelegator.getSprintsByProductId(productId);
			for(int i = 0; i < sprintsJSON.length(); i++) {
				JSONObject sprintJSON = sprintsJSON.getJSONObject(i);
				String sprintId = sprintJSON.getString("sprintId");
				JSONArray committedBacklogItemsJSON = sprintDelegator.getCommittedBacklogItemsBySprintId(sprintId);
				for(int j = 0; j < committedBacklogItemsJSON.length(); j++) {
					JSONObject committedBacklogItemJSON = committedBacklogItemsJSON.getJSONObject(j);
					String committedBacklogItemId = committedBacklogItemJSON.getString("backlogItemId");
					committedBacklogItemIdSet.add(committedBacklogItemId);
				}
			}
			
			int orderId = 0;
			for(int i = 0; i < 3; i++) {
				JSONObject stageJSON = stagesJSON.getJSONObject(i);
				JSONObject miniStageJSON = stageJSON.getJSONArray("miniStageList").getJSONObject(0);
				JSONObject swimLaneJSON = miniStageJSON.getJSONArray("swimLaneList").getJSONObject(0);
				String swimLaneId = swimLaneJSON.getString("swimLaneId");
				JSONArray workItemsJSON = backlogItemDelegator.getWorkItemsBySwimLaneId(swimLaneId);
				for(int j = 0; j < workItemsJSON.length(); j++) {
					JSONObject workItemJSON = workItemsJSON.getJSONObject(j);
					String workItemId = workItemJSON.getString("workItemId");
					if(!committedBacklogItemIdSet.contains(workItemId)) {
						String status = "To do";
						if(i == 1) {
							status = "Doing";
						}else if (i == 2) {
							status = "Done";
						}
						
						JSONObject backlogItemJSON = new JSONObject();
						backlogItemJSON.put("backlogItemId", workItemJSON.getString("workItemId"));
						backlogItemJSON.put("orderId", ++orderId);
						backlogItemJSON.put("description", workItemJSON.getString("description"));
						backlogItemJSON.put("status", status);
						backlogItemJSON.put("estimate", workItemJSON.getInt("estimate"));
						backlogItemJSON.put("importance", backlogItemImportanceDelegator.getBacklogItemImportanceByBacklogItemId(workItemId).getInt("importance"));
						backlogItemJSON.put("notes", workItemJSON.getString("notes"));
						backlogItemJSON.put("productId", productId);
						notYetCommittedBacklogItemList.put(backlogItemJSON);
					}
				}
			}
			getNotYetCommittedBacklogItemsByProductIdOutput.put("notYetCommittedBacklogItemList", notYetCommittedBacklogItemList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getNotYetCommittedBacklogItemsByProductIdOutput.toString();
	}
}
