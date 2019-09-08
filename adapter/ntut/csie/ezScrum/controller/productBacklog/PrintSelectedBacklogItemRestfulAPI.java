package ntut.csie.ezScrum.controller.productBacklog;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

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
import org.json.JSONObject;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.BacklogItemDelegator;
import ntut.csie.ezScrum.controller.delegator.BacklogItemImportanceDelegator;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;
import ntut.csie.ezScrum.controller.maker.PDFMaker;

@Path("/products/{product_id}/printable_backlog_items")
@Singleton
public class PrintSelectedBacklogItemRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private ProductDelegator productDelegator = applicationContext.newProductDelegator();
	private BacklogItemDelegator backlogItemDelegator = applicationContext.newBacklogItemDelegator();
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator = applicationContext.newBacklogItemImportanceDelegator();
	
	@Context
	private ServletContext servletContext; 
	
	@GET
	@Path("/{backlog_item_id}/pdf")
	@Produces("application/pdf")
	public synchronized Response printSelectedBacklogItem(@PathParam("product_id") String productId, 
			@PathParam("backlog_item_id") String backlogItemId) {
		ResponseBuilder responseBuilder = null;
		try {
			JSONArray stagesJSON = productDelegator.getStagesInBoardByProductId(productId);
			List<JSONObject> backlogItemList = new ArrayList<>();
			int orderId = 0;
			boolean isFound = false;
			for(int i = 0; i < 3; i++) {
				JSONObject stageJSON = stagesJSON.getJSONObject(i);
				JSONObject miniStageJSON = stageJSON.getJSONArray("miniStageList").getJSONObject(0);
				JSONObject swimLaneJSON = miniStageJSON.getJSONArray("swimLaneList").getJSONObject(0);
				String swimLaneId = swimLaneJSON.getString("swimLaneId");
				JSONArray workItemsJSON = backlogItemDelegator.getWorkItemsBySwimLaneId(swimLaneId);
				for(int j = 0; j < workItemsJSON.length(); j++) {
					JSONObject workItemJSON = workItemsJSON.getJSONObject(j);
					String workItemId = workItemJSON.getString("workItemId");
					
					if(backlogItemId.equals(workItemId)) {
						JSONObject backlogItemJSON = new JSONObject();
						backlogItemJSON.put("backlogItemId", workItemId);
						backlogItemJSON.put("orderId", ++orderId);
						backlogItemJSON.put("description", workItemJSON.getString("description"));
						backlogItemJSON.put("estimate", workItemJSON.getInt("estimate"));
						backlogItemJSON.put("importance", backlogItemImportanceDelegator.getBacklogItemImportanceByBacklogItemId(workItemId).getInt("importance"));
						backlogItemJSON.put("notes", workItemJSON.getString("notes"));
						backlogItemList.add(backlogItemJSON);
						isFound = true;
					}
				}
				if(isFound) {
					break;
				}
			}

			//直接嵌入server上的pdf字型擋給系統 
			String ttfPath = servletContext.getRealPath("") + "/WEB-INF/otherSetting/uming.ttf";
			
			PDFMaker pdfMaker = new PDFMaker();
			File file = pdfMaker.getBacklogItemFile(ttfPath, backlogItemList);
			
			FileInputStream fileInputStream = new FileInputStream(file);
			responseBuilder = Response.ok((Object) fileInputStream);
			responseBuilder.type("application/pdf");
			responseBuilder.header("Content-Disposition", "filename=backlogItem.pdf");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return responseBuilder.build();
	}
}
