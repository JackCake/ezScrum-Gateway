package ntut.csie.ezScrum.controller.releasePlan;

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
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;
import ntut.csie.ezScrum.controller.delegator.TagDelegator;

@Path("/products/{product_id}/releases/{release_id}/scheduled_backlog_items")
@Singleton
public class GetScheduledBacklogItemsByReleaseIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	private TagDelegator tagDelegator = applicationContext.newTagDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getScheduledBacklogItemsByReleaseId(
			@PathParam("product_id") String productId, 
			@PathParam("release_id") String releaseId) throws JSONException {
		JSONObject getScheduledBacklogItemsByReleaseIdOutput = new JSONObject();
		try {
			JSONArray tagsJSON = tagDelegator.getTagsByProductId(productId);
			Map<String, JSONObject> tagMap = new HashMap<>();
			for(int i = 0; i < tagsJSON.length(); i++) {
				JSONObject tagJSON = tagsJSON.getJSONObject(i);
				String tagId = tagJSON.getString("tagId");
				tagMap.put(tagId, tagJSON);
			}
			
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
					String backlogItemId = workItemJSON.getString("workItemId");
					String status = "To do";
					if(i == 1) {
						status = "Doing";
					}else if (i == 2) {
						status = "Done";
					}
					
					JSONObject backlogItemJSON = new JSONObject();
					backlogItemJSON.put("backlogItemId", backlogItemId);
					backlogItemJSON.put("assignedTagList", getAssignedTagsByBacklogItemId(tagMap, backlogItemId));
					backlogItemJSON.put("description", workItemJSON.getString("description"));
					backlogItemJSON.put("status", status);
					backlogItemJSON.put("estimate", workItemJSON.getInt("estimate"));
					backlogItemJSON.put("importance", backlogItemImportanceDelegator.getBacklogItemImportanceByBacklogItemId(backlogItemId).getInt("importance"));
					backlogItemJSON.put("notes", workItemJSON.getString("notes"));
					backlogItemJSON.put("productId", productId);
					backlogItemMap.put(backlogItemId, backlogItemJSON);
				}
			}
			
			JSONArray scheduledBacklogItemsJSON = releaseDelegator.getScheduledBacklogItemsByReleaseId(releaseId);
			List<JSONObject> scheduledBacklogItemList = new ArrayList<>();
			for(int i = 0 ; i < scheduledBacklogItemsJSON.length(); i++) {
				JSONObject scheduledBacklogItemJSON = scheduledBacklogItemsJSON.getJSONObject(i);
				String backlogItemId = scheduledBacklogItemJSON.getString("backlogItemId");
				scheduledBacklogItemList.add(backlogItemMap.get(backlogItemId));
			}
			
			Collections.sort(scheduledBacklogItemList, new Comparator<JSONObject>() {

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
			for(JSONObject scheduledBacklogItemJSON : scheduledBacklogItemList) {
				scheduledBacklogItemJSON.put("orderId", ++orderId);
			}
			
			getScheduledBacklogItemsByReleaseIdOutput.put("scheduledBacklogItemList", scheduledBacklogItemList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getScheduledBacklogItemsByReleaseIdOutput.toString();
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
