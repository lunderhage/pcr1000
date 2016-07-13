package se.lunderhage.pcr1000.backend.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import se.lunderhage.pcr1000.backend.daemon.PCR1000Impl;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(PCR1000.class.getName(), new PCR1000Impl(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        ServiceReference<?> ref = context.getServiceReference(PCR1000.class.getName());
        PCR1000 pcr1000 = (PCR1000) context.getService(ref);
        pcr1000.stop();
        context.ungetService(ref);
    }

}
