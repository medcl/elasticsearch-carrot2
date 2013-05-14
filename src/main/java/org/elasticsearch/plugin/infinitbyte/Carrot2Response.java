package org.elasticsearch.plugin.infinitbyte;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.internal.InternalSearchResponse;

import java.io.IOException;

import static org.elasticsearch.action.search.ShardSearchFailure.readShardSearchFailure;
import static org.elasticsearch.search.internal.InternalSearchResponse.readInternalSearchResponse;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/20/12
 * Time: 1:58 PM
 */
public class Carrot2Response extends SearchResponse {

   protected final ESLogger logger= Loggers.getLogger(getClass());
    public Carrot2Response(InternalSearchResponse internalResponse, String scrollId, int totalShards, int successfulShards, long tookInMillis, ShardSearchFailure[] shardFailures) {
        this.internalResponse = internalResponse;
        this.scrollId = scrollId;
        this.totalShards = totalShards;
        this.successfulShards = successfulShards;
        this.tookInMillis = tookInMillis;
        this.shardFailures = shardFailures;
    }
    public Carrot2Response(){}

    private InternalSearchResponse internalResponse;

    private String scrollId;

    private int totalShards;

    private int successfulShards;

    private ShardSearchFailure[] shardFailures;

    private long tookInMillis;
    public RestStatus status() {
        if (shardFailures.length == 0) {
            return RestStatus.OK;
        }
        if (successfulShards == 0 && totalShards > 0) {
            RestStatus status = shardFailures[0].status();
            if (shardFailures.length > 1) {
                for (int i = 1; i < shardFailures.length; i++) {
                    if (shardFailures[i].status().getStatus() >= 500) {
                        status = shardFailures[i].status();
                    }
                }
            }
            return status;
        }
        return RestStatus.OK;
    }

    /**
     * The search hits.
     */
    public SearchHits hits() {
        return internalResponse.hits();
    }

    /**
     * The search hits.
     */
    public SearchHits getHits() {
        return hits();
    }

    /**
     * The search facets.
     */
    public Facets facets() {
        return internalResponse.facets();
    }

    /**
     * The search facets.
     */
    public Facets getFacets() {
        return facets();
    }

    /**
     * Has the search operation timed out.
     */
    public boolean timedOut() {
        return internalResponse.timedOut();
    }

    /**
     * Has the search operation timed out.
     */
    public boolean isTimedOut() {
        return timedOut();
    }

    /**
     * How long the search took.
     */
    public TimeValue took() {
        return new TimeValue(tookInMillis);
    }

    /**
     * How long the search took.
     */
    public TimeValue getTook() {
        return took();
    }

    /**
     * How long the search took in milliseconds.
     */
    public long tookInMillis() {
        return tookInMillis;
    }

    /**
     * How long the search took in milliseconds.
     */
    public long getTookInMillis() {
        return tookInMillis();
    }

    /**
     * The total number of shards the search was executed on.
     */
    public int totalShards() {
        return totalShards;
    }

    /**
     * The total number of shards the search was executed on.
     */
    public int getTotalShards() {
        return totalShards;
    }

    /**
     * The successful number of shards the search was executed on.
     */
    public int successfulShards() {
        return successfulShards;
    }

    /**
     * The successful number of shards the search was executed on.
     */
    public int getSuccessfulShards() {
        return successfulShards;
    }

    /**
     * The failed number of shards the search was executed on.
     */
    public int failedShards() {
        return totalShards - successfulShards;
    }

    /**
     * The failed number of shards the search was executed on.
     */
    public int getFailedShards() {
        return failedShards();
    }

    /**
     * The failures that occurred during the search.
     */
    public ShardSearchFailure[] shardFailures() {
        return this.shardFailures;
    }

    /**
     * The failures that occurred during the search.
     */
    public ShardSearchFailure[] getShardFailures() {
        return shardFailures;
    }

    /**
     * If scrolling was enabled ({@link org.elasticsearch.action.search.SearchRequest#scroll(org.elasticsearch.search.Scroll)}, the
     * scroll id that can be used to continue scrolling.
     */
    public String scrollId() {
        return scrollId;
    }

    /**
     * If scrolling was enabled ({@link org.elasticsearch.action.search.SearchRequest#scroll(org.elasticsearch.search.Scroll)}, the
     * scroll id that can be used to continue scrolling.
     */
    public String getScrollId() {
        return scrollId;
    }
    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        if (scrollId != null) {
            builder.field(Fields._SCROLL_ID, scrollId);
        }
        builder.field(Fields.TOOK, tookInMillis);
        builder.field(Fields.TIMED_OUT, timedOut());
        builder.startObject(Fields._SHARDS);
        builder.field(Fields.TOTAL, totalShards());
        builder.field(Fields.SUCCESSFUL, successfulShards());
        builder.field(Fields.FAILED, failedShards());

        if (shardFailures.length > 0) {
            builder.startArray(Fields.FAILURES);
            for (ShardSearchFailure shardFailure : shardFailures) {
                builder.startObject();
                if (shardFailure.shard() != null) {
                    builder.field(Fields.INDEX, shardFailure.shard().index());
                    builder.field(Fields.SHARD, shardFailure.shard().shardId());
                }
                builder.field(Fields.STATUS, shardFailure.status().getStatus());
                builder.field(Fields.REASON, shardFailure.reason());
                builder.endObject();
            }
            builder.endArray();
        }

        builder.endObject();
        internalResponse.toXContent(builder, params);
        return builder;
    }


    static final class Fields {
         static final XContentBuilderString _SCROLL_ID = new XContentBuilderString("_scroll_id");
         static final XContentBuilderString _SHARDS = new XContentBuilderString("_shards");
         static final XContentBuilderString TOTAL = new XContentBuilderString("total");
         static final XContentBuilderString SUCCESSFUL = new XContentBuilderString("successful");
         static final XContentBuilderString FAILED = new XContentBuilderString("failed");
         static final XContentBuilderString FAILURES = new XContentBuilderString("failures");
         static final XContentBuilderString STATUS = new XContentBuilderString("status");
         static final XContentBuilderString INDEX = new XContentBuilderString("index");
         static final XContentBuilderString SHARD = new XContentBuilderString("shard");
         static final XContentBuilderString REASON = new XContentBuilderString("reason");
         static final XContentBuilderString TOOK = new XContentBuilderString("took");
         static final XContentBuilderString TIMED_OUT = new XContentBuilderString("timed_out");
     }


    @Override
    public void readFrom(StreamInput in) throws IOException {
        internalResponse = readInternalSearchResponse(in);
        totalShards = in.readVInt();
        successfulShards = in.readVInt();
        int size = in.readVInt();
        if (size == 0) {
            shardFailures = ShardSearchFailure.EMPTY_ARRAY;
        } else {
            shardFailures = new ShardSearchFailure[size];
            for (int i = 0; i < shardFailures.length; i++) {
                shardFailures[i] = readShardSearchFailure(in);
            }
        }
        if (in.readBoolean()) {
            scrollId = in.readString();
        }
        tookInMillis = in.readVLong();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        internalResponse.writeTo(out);
        out.writeVInt(totalShards);
        out.writeVInt(successfulShards);

        out.writeVInt(shardFailures.length);
        for (ShardSearchFailure shardSearchFailure : shardFailures) {
            shardSearchFailure.writeTo(out);
        }

        if (scrollId == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeString(scrollId);
        }
        out.writeVLong(tookInMillis);
    }

    @Override
    public String toString() {
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder().prettyPrint();
            builder.startObject();
            toXContent(builder, EMPTY_PARAMS);
            builder.endObject();
            return builder.string();
        } catch (IOException e) {
            return "{ \"error\" : \"" + e.getMessage() + "\"}";
        }
    }
}
