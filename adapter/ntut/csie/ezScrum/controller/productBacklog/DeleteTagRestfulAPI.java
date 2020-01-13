package ntut.csie.ezScrum.controller.productBacklog;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import ntut.csie.ezScrum.ApplicationContext;
import ntut.csie.ezScrum.controller.delegator.TagDelegator;

@Path("/tags")
@Singleton
public class DeleteTagRestfulAPI {
	private ApplicationContext applicationContext = ApplicationContext.getInstance();
	private TagDelegator tagDelegator = applicationContext.newTagDelegator();
	
	@DELETE
	@Path("/{tag_id}")
	public synchronized String deleteTag(@PathParam("tag_id") String tagId) {
		Response response = tagDelegator.deleteTag(tagId);
		return response.readEntity(String.class);
	}
}
