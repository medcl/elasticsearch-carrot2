package org.elasticsearch.plugin.infinitbyte;

import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/20/12
 * Time: 6:10 PM
 */
public class Carrot2SourceBuilder extends SearchSourceBuilder{

    private Carrot2Builder carrot2Builder;

    public Carrot2Builder carrot2() {
        if (carrot2Builder == null) {
            carrot2Builder = new Carrot2Builder();
        }
        return carrot2Builder;
    }

    public Carrot2SourceBuilder carrot2(Carrot2Builder carrot2Builder) {
        this.carrot2Builder = carrot2Builder;
        return this;
    }
}
