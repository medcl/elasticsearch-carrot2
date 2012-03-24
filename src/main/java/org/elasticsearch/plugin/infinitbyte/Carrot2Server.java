package org.elasticsearch.plugin.infinitbyte;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/19/12
 * Time: 11:33 PM
 */
public class Carrot2Server extends AbstractLifecycleComponent<Carrot2Server> {
    protected final ESLogger logger= Loggers.getLogger(getClass());

    @Inject
    protected Carrot2Server(Settings settings) {
        super(settings);
    }

    @Override
    protected void doStart() throws ElasticSearchException {

    }

    @Override
    protected void doStop() throws ElasticSearchException {

    }

    @Override
    protected void doClose() throws ElasticSearchException {

    }
}
