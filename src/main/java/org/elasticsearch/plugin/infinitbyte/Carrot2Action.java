package org.elasticsearch.plugin.infinitbyte;

import org.elasticsearch.action.Action;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/20/12
 * Time: 2:04 PM
 */
public class Carrot2Action extends Action<Carrot2Request, Carrot2Response, Carrot2RequestBuilder> {

     public static final Carrot2Action INSTANCE = new Carrot2Action();
     public static final String NAME = "carrot2";
    protected final ESLogger logger= Loggers.getLogger(getClass());
    protected Carrot2Action() {
        super(NAME);
    }

    @Override
    public Carrot2RequestBuilder newRequestBuilder(Client client) {
        return new Carrot2RequestBuilder(client);

    }

    @Override
    public Carrot2Response newResponse() {
        logger.info("carrot2 action newResponse");
        return new Carrot2Response();
    }
}
