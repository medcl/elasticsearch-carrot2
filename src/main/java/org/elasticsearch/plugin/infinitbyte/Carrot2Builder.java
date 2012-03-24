package org.elasticsearch.plugin.infinitbyte;

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/20/12
 * Time: 6:31 PM
 */
public class Carrot2Builder implements ToXContent {

     private String[] TitleFields;
    private String[] SummaryFields;
    private String Language;
    private String Algorithm;
    private int FetchSize=100;


    public String[] getTitleFields() {
        return TitleFields;
    }

    public void setTitleFields(String[] titleFields) {
        TitleFields = titleFields;
    }

    public String[] getSummaryFields() {
        return SummaryFields;
    }

    public void setSummaryFields(String[] summaryFields) {
        SummaryFields = summaryFields;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getAlgorithm() {
        return Algorithm;
    }

    public void setAlgorithm(String algorithm) {
        Algorithm = algorithm;
    }

    public int getFetchSize() {
        return FetchSize;
    }

    public void setFetchSize(int fetchSize) {
        FetchSize = fetchSize;
    }



    @Override
    public XContentBuilder toXContent(XContentBuilder xContentBuilder, Params params) throws IOException {
        return null;
    }
}
