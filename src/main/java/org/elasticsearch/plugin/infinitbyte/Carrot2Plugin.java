package org.elasticsearch.plugin.infinitbyte;

import org.elasticsearch.action.ActionModule;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.rest.RestModule;

import java.util.Collection;

import static org.elasticsearch.common.collect.Lists.newArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/19/12
 * Time: 10:38 PM
 */
public class Carrot2Plugin extends AbstractPlugin{
    protected final ESLogger logger= Loggers.getLogger(getClass());
    private final Settings settings;
      public Carrot2Plugin(Settings settings) {
        this.settings = settings;
    }

    public String name() {
        return "tools-carrot2";
    }

    public String description() {
        return "carrot2 is used for clustering search result";
    }

     /*
	 * @see
	 * org.elasticsearch.plugins.AbstractPlugin#processModule(org.elasticsearch.common.inject.Module)
	 */
    @Override
    public void processModule(Module module) {

    if (module instanceof ActionModule) {
         ((ActionModule) module).registerAction(Carrot2Action.INSTANCE, TransportCarrot2Action.class);
    }

    if (module instanceof RestModule) {
        ((RestModule) module).addRestAction(Carrot2RestAction.class);
    }

    }

    @Override
    public Collection<Class<? extends Module>> modules() {
        Collection<Class<? extends Module>> modules = newArrayList();
        //TODO
        //if (settings.getAsBoolean("carrot2.enabled", true)) {
            modules.add(Carrot2ServerModule.class);
       // }
        return modules;
    }

    @Override
    public Collection<Class<? extends LifecycleComponent>> services() {
        Collection<Class<? extends LifecycleComponent>> services = newArrayList();
        //TODO
        //if (settings.getAsBoolean("carrot2.enabled", true)) {
            services.add(Carrot2Server.class);
        //}
        return services;
    }

}
