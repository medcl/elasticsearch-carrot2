package org.elasticsearch.plugin.infinitbyte;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.search.SearchParseElement;
import org.elasticsearch.search.internal.SearchContext;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/20/12
 * Time: 11:28 AM
 */
public class Carrot2ParseElement implements SearchParseElement {
    protected final ESLogger logger= Loggers.getLogger(getClass());
    @Override
    public void parse(XContentParser xContentParser, SearchContext searchContext) throws Exception {
         //TODO
    }
}
