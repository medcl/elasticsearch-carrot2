package org.elasticsearch.plugin.infinitbyte;

import com.google.common.collect.ImmutableMap;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.search.SearchParseElement;
import org.elasticsearch.search.fetch.FetchSubPhase;
import org.elasticsearch.search.internal.InternalSearchHit;
import org.elasticsearch.search.internal.SearchContext;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/19/12
 * Time: 10:12 PM
 */
public class Carrot2ClusteringPhrase implements FetchSubPhase {
    protected final ESLogger logger= Loggers.getLogger(getClass());

    @Override
    public Map<String, ? extends SearchParseElement> parseElements() {
        return ImmutableMap.of("clusters", new Carrot2ParseElement());
    }

    @Override
    public boolean hitsExecutionNeeded(SearchContext context) {
        return false;
    }

    @Override
    public void hitsExecute(SearchContext context, InternalSearchHit[] hits) throws ElasticSearchException {

    }

    @Override
    public boolean hitExecutionNeeded(SearchContext context) {
        //todo
        return true;
        //return context.cluster() != null;
    }

    @Override
    public void hitExecute(SearchContext context, HitContext hitContext) throws ElasticSearchException {
    }

}
