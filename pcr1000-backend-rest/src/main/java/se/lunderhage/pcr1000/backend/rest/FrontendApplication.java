package se.lunderhage.pcr1000.backend.rest;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class FrontendApplication extends Application<FrontendConfiguration> {

    @Override
    public void run(FrontendConfiguration configuration, Environment environment) throws Exception {
        // TODO Auto-generated method stub

        environment.jersey().register(new PCR1000Bean());

        /*
         * Is it overkill to use dropwizard here?
         *
         * Maybe use plain jersey?
         *
         */

    }


}
