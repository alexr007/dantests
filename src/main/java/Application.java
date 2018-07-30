import core.WholeProcess;
import filters.FilterServletAnybodyLogged;
import filters.FilterServletPostLogin;
import filters.FilterServletPostRegister;
import logic.Persistence;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
//import org.springframework.web.filter.DelegatingFilterProxy;
import servlets.ServletAssets;
import servlets.ServletLogin;
import servlets.ServletLogout;
import servlets.ServletRedirectTo;
import servlets.ServletRegister;
import servlets.ServletStat;
import servlets.ServletTest;
import utils.FreeMarker;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

//import static org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME;

public class Application {
    public static void main(String[] args) throws Exception {
        FreeMarker template = new FreeMarker("./templates");
        WholeProcess wholeProcess = new WholeProcess(new Persistence());
        new Server(8080) {{
            setHandler(new ServletContextHandler(){{
                addServlet(ServletAssets.class, "/assets/*");
                //addServlet(new ServletHolder(new ServletAssets()), "/assets/*");
                addServlet(new ServletHolder(new ServletLogin(wholeProcess, template)), "/login");
                addServlet(new ServletHolder(new ServletLogout(wholeProcess, template)), "/logout");
                addServlet(new ServletHolder(new ServletRegister(wholeProcess, template)), "/register");
                addServlet(new ServletHolder(new ServletTest(wholeProcess, template)), "/test");
                addServlet(new ServletHolder(new ServletStat(wholeProcess, template)), "/stat");
                addServlet(new ServletHolder(new ServletRedirectTo("/login")), "/*");
                //addServlet(new ServletHolder(new ServletMenu(template)),"/*");
                addFilter(FilterServletPostRegister.class, "/register", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
                addFilter(FilterServletPostLogin.class, "/login", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
                addFilter(FilterServletAnybodyLogged.class, "/test", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
                //addFilter(new FilterHolder(new DelegatingFilterProxy(DEFAULT_FILTER_NAME)), "/*", EnumSet.allOf(DispatcherType.class));
                // https://stackoverflow.com/questions/30927761/embedded-jetty-doesnt-recognise-spring-mvc-security
                // https://stackoverflow.com/questions/19718159/java-lang-illegalstateexception-no-sessionmanager/19718918#19718918
            }});
            start();
            join();
        }};
    }
}
