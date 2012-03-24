package org.elasticsearch.plugin.infinitbyte;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.TransportAction;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/20/12
 * Time: 2:08 PM
 */
public class TransportCarrot2Action extends TransportAction<Carrot2Request, Carrot2Response> {

   protected final ESLogger logger= Loggers.getLogger(getClass());

    @Inject
    protected TransportCarrot2Action(Settings settings, ThreadPool threadPool) {
        super(settings, threadPool);
    }


    @Override
    protected void doExecute(Carrot2Request carrot2Request, ActionListener<Carrot2Response> carrot2ResponseActionListener) {

    }
}
