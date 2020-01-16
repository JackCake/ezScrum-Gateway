package ntut.csie.ezScrum.controller.sprintBacklog;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.BacklogItemAttachFileDelegator;

@Path("/backlog_item_attach_files")
@Singleton
public class DownloadBacklogItemAttachFileRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private BacklogItemAttachFileDelegator backlogItemAttachFileDelegator = applicationContext.newBacklogItemAttachFileDelegator();
	
	@GET
	@Path("/{backlog_item_attach_file_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String downloadBacklogItemAttachFile(
			@PathParam("backlog_item_attach_file_id") String backlogItemAttachFileId) {
		Response response = backlogItemAttachFileDelegator.downloadBacklogItemAttachFile(backlogItemAttachFileId);
		return response.readEntity(String.class);
	}
}
