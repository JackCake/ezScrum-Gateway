package ntut.csie.ezScrum.controller.sprintBacklog;

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
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;
import ntut.csie.ezScrum.controller.delegator.TaskDelegator;
import ntut.csie.ezScrum.controller.maker.PDFMaker;

@Path("/products/{product_id}/sprints/{sprint_id}/printable_tasks")
@Singleton
public class PrintTasksRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private SprintDelegator sprintDelegator = applicationContext.newSprintDelegator();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();
	
	@Context
	private ServletContext servletContext; 
	
	@GET
	@Path("/pdf")
	@Produces("application/pdf")
	public synchronized Response getTaskBurndownChart(
			@PathParam("product_id") String productId, 
			@PathParam("sprint_id") String sprintId) {
		ResponseBuilder responseBuilder = null;
		try {
			JSONArray committedBacklogItemsJSON = sprintDelegator.getCommittedBacklogItemsBySprintId(sprintId);
			List<JSONObject> taskList = new ArrayList<>();
			for(int i = 0; i < committedBacklogItemsJSON.length(); i++) {
				JSONObject committedBacklogItemJSON = committedBacklogItemsJSON.getJSONObject(i);
				String backlogItemId = committedBacklogItemJSON.getString("backlogItemId");
				JSONArray tasksJSON = taskDelegator.getTasksByWorkItemId(backlogItemId);
				for(int j = 0; j < tasksJSON.length(); j++) {
					taskList.add(tasksJSON.getJSONObject(j));
				}
			}
			
			//直接嵌入server上的pdf字型檔給系統 
			String ttfPath = servletContext.getRealPath("") + "/WEB-INF/otherSetting/uming.ttf";
			
			PDFMaker pdfMaker = new PDFMaker();
			File file = pdfMaker.getTaskFile(ttfPath, taskList);
			
			FileInputStream fileInputStream = new FileInputStream(file);
			responseBuilder = Response.ok((Object) fileInputStream);
			responseBuilder.type("application/pdf");
			responseBuilder.header("Content-Disposition", "filename=tasks.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseBuilder.build();
	}
}
