package ntut.csie.ezScrum;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public class ezScrumGatewayStart extends HttpServlet implements ServletContextListener{
	
	  @Override
	  public void contextDestroyed(ServletContextEvent arg0) {
		
	  }

	  @Override
	  public void contextInitialized(ServletContextEvent arg0) {
		  System.out.println("ezScrum Gateway Start!");
		  ApplicationContext.getInstance();
	  }

}
