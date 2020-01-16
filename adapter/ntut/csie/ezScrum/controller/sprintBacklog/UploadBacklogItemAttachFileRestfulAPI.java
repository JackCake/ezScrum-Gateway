package ntut.csie.ezScrum.controller.sprintBacklog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.BacklogItemAttachFileDelegator;

@Path("/committed_backlog_items/{backlog_item_id}/backlog_item_attach_files")
@Singleton
public class UploadBacklogItemAttachFileRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private BacklogItemAttachFileDelegator backlogItemAttachFileDelegator = applicationContext.newBacklogItemAttachFileDelegator();
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized String uploadBacklogItemAttachFile(
			@PathParam("backlog_item_id") String backlogItemId, 
			@FormDataParam("attach_file") InputStream uploadedAttachFileInputStream, 
			@FormDataParam("attach_file") FormDataContentDisposition attachFileDetail) {
		String responseString = "";
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int numberOfReadedBytes = 0;
			byte[] readedBytes = new byte[1024];
			while((numberOfReadedBytes = uploadedAttachFileInputStream.read(readedBytes, 0, readedBytes.length)) != -1) {
				buffer.write(readedBytes, 0, numberOfReadedBytes);
			}
			String attachFileContent = Base64.encode(buffer.toByteArray());
			String fileName = new String(attachFileDetail.getFileName().getBytes("iso-8859-1"), "UTF-8");
			Response response = backlogItemAttachFileDelegator.uploadBacklogItemAttachFile(attachFileContent, fileName, backlogItemId);
			responseString = response.readEntity(String.class);
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> uploadBacklogItemAttachFileOutputMap = new HashMap<>();
			uploadBacklogItemAttachFileOutputMap.put("uploadSuccess", false);
			uploadBacklogItemAttachFileOutputMap.put("errorMessage", "Sorry, please try again!");
			JSONObject uploadBacklogItemAttachFileOutputJSON = new JSONObject(uploadBacklogItemAttachFileOutputMap);
			return uploadBacklogItemAttachFileOutputJSON.toString();
		}
		return responseString;
	}
}