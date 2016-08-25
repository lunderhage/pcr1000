package se.lunderhage.pcr1000.backend.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.lunderhage.pcr1000.backend.daemon.PCR1000Impl;
import se.lunderhage.pcr1000.backend.model.PCR1000;

public class Activator implements BundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        PCR1000 pcr1000 = new PCR1000Impl();
        context.registerService(PCR1000.class.getName(), pcr1000, null);
        pcr1000.start();
        LOG.info("PCR1000 service is registered.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        ServiceReference<?> ref = context.getServiceReference(PCR1000.class.getName());
        PCR1000 pcr1000 = (PCR1000) context.getService(ref);
        pcr1000.stop();
        context.ungetService(ref);
        LOG.info("PCR1000 service is unregistered.");
    }

}
