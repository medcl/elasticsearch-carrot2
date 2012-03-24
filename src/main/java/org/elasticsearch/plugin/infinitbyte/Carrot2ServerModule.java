package org.elasticsearch.plugin.infinitbyte;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/19/12
 * Time: 11:28 PM
 */
public class Carrot2ServerModule extends AbstractModule {

    private final Settings settings;
       protected final ESLogger logger= Loggers.getLogger(getClass());
    public Carrot2ServerModule(Settings settings) {
        this.settings = settings;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    protected void configure() {
       // bind(Carrot2RestImpl.class).asEagerSingleton();
        //bind(Carrot2Server.class).asEagerSingleton();
        bind(Carrot2ClusteringPhrase.class).asEagerSingleton();
    }
}
