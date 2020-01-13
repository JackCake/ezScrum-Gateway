package ntut.csie.ezScrum.controller.productBacklog;

import java.util.HashMap;
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
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;
import ntut.csie.ezScrum.controller.delegator.TagDelegator;

@Path("/products/{product_id}/backlog_items")
@Singleton
public class GetBacklogItemsByProductIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	private TagDelegator tagDelegator = applicationContext.newTagDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getBacklogItemsByProductId(@PathParam("product_id") String productId) {
		JSONObject getBacklogItemsByProductIdOutput = new JSONObject();
		try {
			JSONArray tagsJSON = tagDelegator.getTagsByProductId(productId);
			Map<String, JSONObject> tagMap = new HashMap<>();
			for(int i = 0; i < tagsJSON.length(); i++) {
				JSONObject tagJSON = tagsJSON.getJSONObject(i);
				String tagId = tagJSON.getString("tagId");
				tagMap.put(tagId, tagJSON);
			}
			
			Map<String, Integer> releaseMap = new HashMap<>();
			JSONArray releasesJSON = releaseDelegator.getReleasesByProductId(productId);
			for(int i = 0 ; i < releasesJSON.length(); i++) {
				JSONObject releaseJSON = releasesJSON.getJSONObject(i);
				releaseMap.put(releaseJSON.getString("releaseId"), releaseJSON.getInt("orderId"));
			}
			
			Map<String, String> scheduledBacklogItemMap = new HashMap<>();
			for(String releaseId : releaseMap.keySet()) {
				JSONArray scheduledBacklogItemsJSON = releaseDelegator.getScheduledBacklogItemsByReleaseId(releaseId);
				for(int i = 0; i < scheduledBacklogItemsJSON.length(); i ++) {
					JSONObject scheduledBacklogItemJSON = scheduledBacklogItemsJSON.getJSONObject(i);
					scheduledBacklogItemMap.put(scheduledBacklogItemJSON.getString("backlogItemId"), releaseId);
				}
			}
			
			Map<String, Integer> sprintMap = new HashMap<>();
			JSONArray sprintsJSON = sprintDelegator.getSprintsByProductId(productId);
			for(int i = 0 ; i < sprintsJSON.length(); i++) {
				JSONObject sprintJSON = sprintsJSON.getJSONObject(i);
				sprintMap.put(sprintJSON.getString("sprintId"), sprintJSON.getInt("orderId"));
			}
			
			Map<String, String> committedBacklogItemMap = new HashMap<>();
			for(String sprintId : sprintMap.keySet()) {
				JSONArray committedBacklogItemsJSON = sprintDelegator.getCommittedBacklogItemsBySprintId(sprintId);
				for(int i = 0; i < committedBacklogItemsJSON.length(); i ++) {
					JSONObject committedBacklogItemJSON = committedBacklogItemsJSON.getJSONObject(i);
					committedBacklogItemMap.put(committedBacklogItemJSON.getString("backlogItemId"), sprintId);
				}
			}
			
			JSONArray stagesJSON = productDelegator.getStagesInBoardByProductId(productId);
			JSONArray backlogItemList = new JSONArray();
			int orderId = 0;
			for(int i = 0; i < 3; i++) {
				JSONObject stageJSON = stagesJSON.getJSONObject(i);
				JSONObject miniStageJSON = stageJSON.getJSONArray("miniStageList").getJSONObject(0);
				JSONObject swimLaneJSON = miniStageJSON.getJSONArray("swimLaneList").getJSONObject(0);
				String swimLaneId = swimLaneJSON.getString("swimLaneId");
				JSONArray workItemsJSON = backlogItemDelegator.getWorkItemsBySwimLaneId(swimLaneId);
				for(int j = 0; j < workItemsJSON.length(); j++) {
					JSONObject workItemJSON = workItemsJSON.getJSONObject(j);
					String status = "To do";
					if(i == 1) {
						status = "Doing";
					}else if (i == 2) {
						status = "Done";
					}
					int releaseOrderId = 0;
					int sprintOrderId = 0;
					String backlogItemId = workItemJSON.getString("workItemId");
					if(scheduledBacklogItemMap.containsKey(backlogItemId)) {
						String releaseId = scheduledBacklogItemMap.get(backlogItemId);
						releaseOrderId = releaseMap.get(releaseId);
					}
					if(committedBacklogItemMap.containsKey(backlogItemId)) {
						String sprintId = committedBacklogItemMap.get(backlogItemId);
						sprintOrderId = sprintMap.get(sprintId);
					}
					
					JSONObject backlogItemJSON = new JSONObject();
					backlogItemJSON.put("backlogItemId", backlogItemId);
					backlogItemJSON.put("orderId", ++orderId);
					backlogItemJSON.put("assignedTagList", getAssignedTagsByBacklogItemId(tagMap, backlogItemId));
					backlogItemJSON.put("description", workItemJSON.getString("description"));
					backlogItemJSON.put("status", status);
					backlogItemJSON.put("estimate", workItemJSON.getInt("estimate"));
					backlogItemJSON.put("importance", backlogItemImportanceDelegator.getBacklogItemImportanceByBacklogItemId(backlogItemId).getInt("importance"));
					backlogItemJSON.put("notes", workItemJSON.getString("notes"));
					backlogItemJSON.put("productId", productId);
					backlogItemJSON.put("releaseOrderId", releaseOrderId);
					backlogItemJSON.put("sprintOrderId", sprintOrderId);
					backlogItemList.put(backlogItemJSON);
				}
			}
			
			getBacklogItemsByProductIdOutput.put("backlogItemList", backlogItemList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getBacklogItemsByProductIdOutput.toString();
	}
	
	private JSONArray getAssignedTagsByBacklogItemId(Map<String, JSONObject> tagMap, String backlogItemId) throws JSONException {
		JSONArray assignedTags = new JSONArray();
		JSONArray assignedTagsJSON = tagDelegator.getAssignedTagsByBacklogItemId(backlogItemId);
		for(int i = 0; i < assignedTagsJSON.length(); i++) {
			JSONObject assignedTagJSON = assignedTagsJSON.getJSONObject(i);
			String tagId = assignedTagJSON.getString("tagId");
			assignedTags.put(tagMap.get(tagId));
		}
		return assignedTags;
	}
}
