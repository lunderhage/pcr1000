package se.lunderhage.pcr1000.backend.rest;

import org.eclipse.jetty.server.Server;
import org.osgi.service.component.annotations.Component;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;

@Component
public class EmbeddedServerCommand<T extends Configuration> extends EnvironmentCommand<T> {

    private Server server;

    protected EmbeddedServerCommand(Application<T> application, String name, String description) {
        super(application, name, description);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void run(Environment environment, Namespace namespace, T configuration) throws Exception {
        server = configuration.getServerFactory().build(environment);
        try {
            server.start();
        } catch (Exception e) {
            server.stop();
            throw e;
        }
    }

    public void stop() {
        if (server != null) {
            try {
                server.stop();
                if (!server.isStopped())
                    throw new RuntimeException("Failed to stop Jetty.");
            } catch (Exception e) {
                throw new RuntimeException("Failed to stop Jetty.", e);
            }
        }
    }

}
