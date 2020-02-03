package ntut.csie.ezScrum.controller.sprintBacklog;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.TaskDelegator;

@Path("/tasks/{task_id}/task_attach_files")
@Singleton
public class DownloadTaskAttachFileRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();
	
	@GET
	@Path("/{task_attach_file_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String downloadBacklogItemAttachFile(
			@PathParam("task_id") String taskId, 
			@PathParam("task_attach_file_id") String taskAttachFileId) {
		Response response = taskDelegator.downloadTaskAttachFile(taskAttachFileId, taskId);
		return response.readEntity(String.class);
	}
}
