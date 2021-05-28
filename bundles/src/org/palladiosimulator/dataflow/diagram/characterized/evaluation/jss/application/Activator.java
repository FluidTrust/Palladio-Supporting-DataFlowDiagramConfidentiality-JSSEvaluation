package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.palladiosimulator.supporting.prolog.api.PrologAPI;
import org.prolog4j.manager.IProverManager;

public class Activator extends Plugin {

    private static Activator instance = null;
    private ServiceReference<IProverManager> proverManagerServiceReference;
    private IProverManager proverManager;
    private ServiceReference<PrologAPI> prologAPIServiceReference;
    private PrologAPI prologAPI;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        setInstance(this);

        this.proverManagerServiceReference = context.getServiceReference(IProverManager.class);
        this.proverManager = context.getService(this.proverManagerServiceReference);

        this.prologAPIServiceReference = context.getServiceReference(PrologAPI.class);
        this.prologAPI = context.getService(this.prologAPIServiceReference);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        this.proverManager = null;
        context.ungetService(this.proverManagerServiceReference);

        this.prologAPI = null;
        context.ungetService(this.prologAPIServiceReference);

        setInstance(null);
        super.stop(context);
    }

    private static void setInstance(Activator instance) {
        Activator.instance = instance;
    }

    public static Activator getInstance() {
        return instance;
    }

    public IProverManager getProverManager() {
        return proverManager;
    }

    public PrologAPI getPrologAPI() {
        return prologAPI;
    }

}
