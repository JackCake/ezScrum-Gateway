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
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.BacklogItemDelegator;
import ntut.csie.ezScrum.controller.delegator.BacklogItemImportanceDelegator;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;
import ntut.csie.ezScrum.controller.maker.HTMLMaker;

@Path("/products/{product_id}/sprints/{sprint_id}/sprint_information")
@Singleton
public class ShowSprintInformationRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public synchronized Response getSprintInformation(
			@PathParam("product_id") String productId, 
			@PathParam("sprint_id") String sprintId) {
		String result = "";
		try {
			JSONObject sprintJSON = getSprint(sprintId, productId);
			
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
			
			HTMLMaker htmlMaker = new HTMLMaker();
			result = htmlMaker.getSprintInformationHtml(sprintJSON, committedBacklogItemList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(200).entity(result).build();
	}
	
	private JSONObject getSprint(String sprintId, String productId) throws JSONException {
		JSONArray sprintsJSON = sprintDelegator.getSprintsByProductId(productId);
		for(int i = 0; i < sprintsJSON.length(); i++) {
			JSONObject sprintJSON = sprintsJSON.getJSONObject(i);
			if(sprintJSON.getString("sprintId").equals(sprintId)) {
				return sprintJSON;
			}
		}
		return null;
	}
}
