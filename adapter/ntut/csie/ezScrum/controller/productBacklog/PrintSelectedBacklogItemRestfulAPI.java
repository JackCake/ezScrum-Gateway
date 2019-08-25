package ntut.csie.ezScrum.controller.productBacklog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.struts.actions.DownloadAction.FileStreamInfo;
import org.apache.struts.actions.DownloadAction.StreamInfo;
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
	
	@GET
	@Path("/{backlog_item_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized StreamInfo printSelectedBacklogItem(@PathParam("product_id") String productId, 
			@PathParam("backlog_item_id") String backlogItemId) {
		File file = null;

		try {
			//直接嵌入server上的pdf字型擋給系統 
			ClassLoader classLoader = this.getClass().getClassLoader();
			String ttfPath = classLoader.getResource("/").getPath() + "/otherSetting/uming.ttf";
System.out.println(ttfPath);
			
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

			PDFMaker pdfMaker = new PDFMaker();
			file = pdfMaker.getBacklogItemFile(ttfPath, backlogItemList);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		String contentType = "application/pdf";
		return new FileStreamInfo(contentType, file);
	}
}
