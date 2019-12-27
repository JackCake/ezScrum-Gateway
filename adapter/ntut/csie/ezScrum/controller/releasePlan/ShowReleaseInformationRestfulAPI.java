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
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.BacklogItemDelegator;
import ntut.csie.ezScrum.controller.delegator.BacklogItemImportanceDelegator;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;
import ntut.csie.ezScrum.controller.maker.HTMLMaker;

@Path("/products/{product_id}/releases/{release_id}/release_information")
@Singleton
public class ShowReleaseInformationRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private ReleaseDelegator releaseDelegator = applicationContext.newReleaseDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public synchronized Response getReleaseInformation(
			@PathParam("product_id") String productId, 
			@PathParam("release_id") String releaseId) {
		String result = "";
		try {
			JSONObject releaseJSON = getRelease(releaseId, productId);
			
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
					
					JSONObject backlogItemJSON = new JSONObject();
					backlogItemJSON.put("backlogItemId", workItemJSON.getString("workItemId"));
					backlogItemJSON.put("description", workItemJSON.getString("description"));
					backlogItemJSON.put("estimate", workItemJSON.getInt("estimate"));
					backlogItemJSON.put("importance", backlogItemImportanceDelegator.getBacklogItemImportanceByBacklogItemId(workItemId).getInt("importance"));
					backlogItemMap.put(workItemId, backlogItemJSON);
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
			
			HTMLMaker htmlMaker = new HTMLMaker();
			result = htmlMaker.getReleaseInformationHtml(releaseJSON, scheduledBacklogItemList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(200).entity(result).build();
	}
	
	private JSONObject getRelease(String releaseId, String productId) throws JSONException {
		JSONArray releasesJSON = releaseDelegator.getReleasesByProductId(productId);
		for(int i = 0; i < releasesJSON.length(); i++) {
			JSONObject releaseJSON = releasesJSON.getJSONObject(i);
			if(releaseJSON.getString("releaseId").equals(releaseId)) {
				return releaseJSON;
			}
		}
		return null;
	}
}
