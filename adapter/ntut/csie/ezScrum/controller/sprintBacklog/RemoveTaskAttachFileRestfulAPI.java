package ntut.csie.ezScrum.controller.sprintBacklog;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.TaskDelegator;

@Path("/tasks/{task_id}/task_attach_files")
@Singleton
public class RemoveTaskAttachFileRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TaskDelegator taskDelegator = applicationContext.newTaskDelegator();
	
	@DELETE
	@Path("/{task_attach_file_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String removeBacklogItemAttachFile(
			@PathParam("task_id") String taskId, 
			@PathParam("task_attach_file_id") String taskAttachFileId) {
		Response response = taskDelegator.removeTaskAttachFile(taskAttachFileId, taskId);
		return response.readEntity(String.class);
	}
}