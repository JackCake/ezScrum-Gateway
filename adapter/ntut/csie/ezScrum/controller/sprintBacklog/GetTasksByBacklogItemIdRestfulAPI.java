package ntut.csie.ezScrum.controller.sprintBacklog;

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
import ntut.csie.ezScrum.controller.delegator.TaskDelegator;

@Path("committed_backlog_items/{backlog_item_id}/tasks")
@Singleton
public class GetTasksByBacklogItemIdRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String getTasksByBacklogItemId(@PathParam("backlog_item_id") String backlogItemId) {
		JSONObject getTasksByBacklogItemIdOutput = new JSONObject();
		try {
			JSONArray taskList = taskDelegator.getTasksByWorkItemId(backlogItemId);
			
			for(int i = 0; i < taskList.length(); i++) {
				String taskId = taskList.getJSONObject(i).getString("taskId");
				taskList.getJSONObject(i).put("attachFileList", taskDelegator.getTaskAttachFilesByTaskId(taskId));
			}
			
			getTasksByBacklogItemIdOutput.put("taskList", taskList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getTasksByBacklogItemIdOutput.toString();
	}
}
