package org.elasticsearch.plugin.infinitbyte;

import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestController;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/19/12
 * Time: 11:31 PM
 */
public class Carrot2RestImpl extends AbstractComponent {
    private final RestController restController;
      protected final ESLogger logger= Loggers.getLogger(getClass());
    @Inject
    public Carrot2RestImpl(Settings settings, RestController restController) {
        super(settings);
        this.restController = restController;
    }

}
