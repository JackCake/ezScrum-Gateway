package ntut.csie.ezScrum.controller.sprintBacklog;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.BacklogItemDelegator;
import ntut.csie.ezScrum.controller.delegator.BacklogItemImportanceDelegator;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;
import ntut.csie.ezScrum.controller.maker.PDFMaker;

@Path("/products/{product_id}/sprints/{sprint_id}/printable_committed_backlog_items")
@Singleton
public class PrintCommittedBacklogItemsRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	
	@Context
	private ServletContext servletContext; 
	
	@GET
	@Path("/pdf")
	@Produces("application/pdf")
	public synchronized Response printCommittedBacklogItems(
			@PathParam("product_id") String productId, 
			@PathParam("sprint_id") String sprintId) {
		ResponseBuilder responseBuilder = null;
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
					
					JSONObject backlogItemJSON = new JSONObject();
					backlogItemJSON.put("backlogItemId", workItemJSON.getString("workItemId"));
					backlogItemJSON.put("description", workItemJSON.getString("description"));
					backlogItemJSON.put("estimate", workItemJSON.getInt("estimate"));
					backlogItemJSON.put("importance", backlogItemImportanceDelegator.getBacklogItemImportanceByBacklogItemId(workItemId).getInt("importance"));
					backlogItemJSON.put("notes", workItemJSON.getString("notes"));
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
			
			//直接嵌入server上的pdf字型擋給系統 
			String ttfPath = servletContext.getRealPath("") + "/WEB-INF/otherSetting/uming.ttf";
			
			PDFMaker pdfMaker = new PDFMaker();
			File file = pdfMaker.getBacklogItemFile(ttfPath, committedBacklogItemList);
			
			FileInputStream fileInputStream = new FileInputStream(file);
			responseBuilder = Response.ok((Object) fileInputStream);
			responseBuilder.type("application/pdf");
			responseBuilder.header("Content-Disposition", "filename=backlogItems.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseBuilder.build();
	}
}
