package ntut.csie.ezScrum.controller.releasePlan;

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
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;

@Path("/products/{product_id}/not_yet_scheduled_backlog_items")
@Singleton
public class GetNotYetScheduledBacklogItemsByProductIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getNotYetScheduledBacklogItemsByProductId(@PathParam("product_id") String productId) {
		JSONObject getNotYetScheduledBacklogItemsByProductIdOutput = new JSONObject();
		try {
			JSONArray notYetScheduledBacklogItemList = new JSONArray();
			
			Set<String> scheduledBacklogItemIdSet = new HashSet<>();
			JSONArray releasesJSON = releaseDelegator.getReleasesByProductId(productId);
			for(int i = 0; i < releasesJSON.length(); i++) {
				JSONObject releaseJSON = releasesJSON.getJSONObject(i);
				String releaseId = releaseJSON.getString("releaseId");
				JSONArray scheduledBacklogItemsJSON = releaseDelegator.getScheduledBacklogItemsByReleaseId(releaseId);
				for(int j = 0; j < scheduledBacklogItemsJSON.length(); j++) {
					JSONObject scheduledBacklogItemJSON = scheduledBacklogItemsJSON.getJSONObject(j);
					String scheduledBacklogItemId = scheduledBacklogItemJSON.getString("backlogItemId");
					scheduledBacklogItemIdSet.add(scheduledBacklogItemId);
				}
			}
			
			JSONArray stagesJSON = productDelegator.getStagesInBoardByProductId(productId);
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
					if(!scheduledBacklogItemIdSet.contains(workItemId)) {
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
						notYetScheduledBacklogItemList.put(backlogItemJSON);
					}
				}
			}
			getNotYetScheduledBacklogItemsByProductIdOutput.put("notYetScheduledBacklogItemList", notYetScheduledBacklogItemList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getNotYetScheduledBacklogItemsByProductIdOutput.toString();
	}
}
