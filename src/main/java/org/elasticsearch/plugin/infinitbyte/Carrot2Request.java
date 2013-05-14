package org.elasticsearch.plugin.infinitbyte;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/20/12
 * Time: 1:57 PM
 */
public class Carrot2Request extends SearchRequest {
protected final ESLogger logger= Loggers.getLogger(getClass());

    public String[] TitleFields;
    public String[] SummaryFields;
    public String   UrlField;
    public String Language;
    public String Algorithm;
    public int FetchSize=20;
    public int MaxDocPerCluster=10;
    public int MaxClusters=10;

    public boolean AttachDetail =false;
    public boolean AttachSourceHits =false;
    public int DesiredClusterCountBase=10;
    public double PhraseLabelBoost=1.5;


    public Carrot2Request(String[] indices) {
        super(indices);
    }
    public Carrot2Request() {
        }

      public Carrot2Request(String[] indices, byte[] source) {
        super(indices,source);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeVInt(TitleFields.length);
        for (String title : TitleFields) {
            out.writeString(title);
        }

        out.writeVInt(SummaryFields.length);
        for (String summary : SummaryFields) {
            out.writeString(summary);
        }

        out.writeString(Language);
        out.writeString(Algorithm);
        out.writeString(UrlField);
        out.writeInt(FetchSize);
        out.writeInt(DesiredClusterCountBase);
        out.writeDouble(PhraseLabelBoost);

    }

    @Override
    public void readFrom(StreamInput in) throws IOException {

        super.readFrom(in);

        TitleFields = new String[in.readVInt()];
        for (int i = 0; i < TitleFields.length; i++) {
            TitleFields[i] = in.readString();
        }

        SummaryFields = new String[in.readVInt()];
        for (int i = 0; i < SummaryFields.length; i++) {
            SummaryFields[i] = in.readString();
        }

        Language=in.readString();
        Algorithm=in.readString();
        UrlField=in.readString();
        FetchSize=in.readInt();
        DesiredClusterCountBase=in.readInt();
        PhraseLabelBoost=in.readDouble();

    }
}
