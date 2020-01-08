package ntut.csie.ezScrum;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import ntut.csie.ezScrum.controller.delegator.BacklogItemDelegator;
import ntut.csie.ezScrum.controller.delegator.BacklogItemImportanceDelegator;
import ntut.csie.ezScrum.controller.delegator.ProductDelegator;
import ntut.csie.ezScrum.controller.delegator.ReleaseDelegator;
import ntut.csie.ezScrum.controller.delegator.SprintDelegator;
import ntut.csie.ezScrum.controller.delegator.TagDelegator;
import ntut.csie.ezScrum.controller.delegator.TaskDelegator;

public class ApplicationContext {
	private static ApplicationContext instance = null;
	
	private Client client;

	private ProductDelegator productDelegator;
	private BacklogItemDelegator backlogItemDelegator;
	private BacklogItemImportanceDelegator backlogItemImportanceDelegator;
	private TagDelegator tagDelegator;
	private ReleaseDelegator releaseDelegator;
	private SprintDelegator sprintDelegator;
	private TaskDelegator taskDelegator;
	
	private ApplicationContext() {
		ClientConfig config = new ClientConfig();
	    config.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
		client = ClientBuilder.newClient(config);
	}
	
	public static synchronized ApplicationContext getInstance() {
		if(instance == null) {
			instance = new ApplicationContext();
		}
		return instance;
	}
	
	public ProductDelegator newProductDelegator(){
		productDelegator = new ProductDelegator(client);
		return productDelegator;
	}
	
	public BacklogItemDelegator newBacklogItemDelegator(){
		backlogItemDelegator = new BacklogItemDelegator(client);
		return backlogItemDelegator;
	}
	
	public BacklogItemImportanceDelegator newBacklogItemImportanceDelegator(){
		backlogItemImportanceDelegator = new BacklogItemImportanceDelegator(client);
		return backlogItemImportanceDelegator;
	}
	
	public TagDelegator newTagDelegator() {
		tagDelegator = new TagDelegator(client);
		return tagDelegator;
	}
	
	public ReleaseDelegator newReleaseDelegator() {
		releaseDelegator = new ReleaseDelegator(client);
		return releaseDelegator;
	}
	
	public SprintDelegator newSprintDelegator(){
		sprintDelegator = new SprintDelegator(client);
		return sprintDelegator;
	}
	
	public TaskDelegator newTaskDelegator(){
		taskDelegator = new TaskDelegator(client);
		return taskDelegator;
	}
}
