package org.elasticsearch.plugin.infinitbyte;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithmDescriptor;
import org.carrot2.core.*;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.matrix.factorization.IterationNumberGuesser;
import org.carrot2.matrix.factorization.LocalNonnegativeMatrixFactorizationFactory;
import org.carrot2.text.clustering.MultilingualClusteringDescriptor;
import org.carrot2.text.linguistic.DefaultLexicalDataFactoryDescriptor;
import org.carrot2.text.linguistic.LexicalDataLoaderDescriptor;
import org.carrot2.util.resource.DirLocator;
import org.carrot2.util.resource.ResourceLookup;
import org.elasticsearch.ElasticSearchIllegalArgumentException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchOperationThreading;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.rest.*;
import org.elasticsearch.rest.action.support.RestActions;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.unit.TimeValue.parseTimeValue;
import static org.elasticsearch.rest.RestStatus.BAD_REQUEST;
import static org.elasticsearch.rest.action.support.RestXContentBuilder.restContentBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/20/12
 * Time: 3:45 PM
 */
public class Carrot2RestAction extends BaseRestHandler {

    final Controller controller = ControllerFactory.createCachingPooling(IDocumentSource.class,LingoClusteringAlgorithm.class);
    Environment environment;
    @Inject
    protected Carrot2RestAction(Settings settings, Client client, RestController restController) {
        super(settings, client);

        environment=new Environment(settings);

//        logger.debug(environment.configFile().getPath());
//        logger.debug(environment.configFile().getAbsolutePath());

        restController.registerHandler(RestRequest.Method.POST, "/{index}/{type}/_carrot2", this);
        restController.registerHandler(RestRequest.Method.POST, "/{index}/{type}/_carrot2/{algorithm}", this);
        restController.registerHandler(RestRequest.Method.POST, "/{index}/_carrot2", this);
        restController.registerHandler(RestRequest.Method.POST, "/{index}/_carrot2/{algorithm}", this);
        restController.registerHandler(RestRequest.Method.POST, "/_carrot2/{algorithm}", this);
        restController.registerHandler(RestRequest.Method.POST, "/_carrot2", this);

        restController.registerHandler(RestRequest.Method.POST, "/{index}/{type}/_search_clustering", this);
        restController.registerHandler(RestRequest.Method.POST, "/{index}/{type}/_search_clustering/{algorithm}", this);
        restController.registerHandler(RestRequest.Method.POST, "/{index}/_search_clustering", this);
        restController.registerHandler(RestRequest.Method.POST, "/{index}/_search_clustering/{algorithm}", this);
        restController.registerHandler(RestRequest.Method.POST, "/_search_clustering/{algorithm}", this);
        restController.registerHandler(RestRequest.Method.POST, "/_search_clustering", this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel) {
        final Carrot2Request searchRequest;
        try {
            searchRequest = parseSearchRequest(request);
            searchRequest.listenerThreaded(false);
            SearchOperationThreading operationThreading = SearchOperationThreading.fromString(request.param("operation_threading"), null);
            if (operationThreading != null) {
                if (operationThreading == SearchOperationThreading.NO_THREADS) {
                    // since we don't spawn, don't allow no_threads, but change it to a single thread
                    operationThreading = SearchOperationThreading.SINGLE_THREAD;
                }
                searchRequest.operationThreading(operationThreading);
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("failed to parse search request parameters", e);
            }
            try {
                XContentBuilder builder = restContentBuilder(request);
                channel.sendResponse(new XContentRestResponse(request, BAD_REQUEST, builder.startObject().field("error", e.getMessage()).endObject()));
            } catch (IOException e1) {
                logger.error("Failed to send failure response", e1);
            }
            return;
        }

        client.search(searchRequest, new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse response) {

                Carrot2Request req=(Carrot2Request)searchRequest;

                ProcessingResult processingResult = null;
                try {

                    final List<Document> documents = Lists.newArrayList();
                    if(req!=null){
                        if(logger.isDebugEnabled()){
                         logger.debug(String.valueOf(req.FetchSize));
                         logger.debug(String.valueOf(req.DesiredClusterCountBase));
                         logger.debug(String.valueOf(req.PhraseLabelBoost));
                         logger.debug(String.valueOf(req.Algorithm));
                         logger.debug(String.valueOf(req.Language));
                         logger.debug(java.util.Arrays.toString(req.TitleFields));
                         logger.debug(java.util.Arrays.toString(req.SummaryFields));
                         logger.debug(req.UrlField);
                        }
                    }

                    for (SearchHit hit : response.getHits()) {


                        Map<String, Object> objectMap = hit.sourceAsMap();
                        if(objectMap==null){
                            if(hit.fields()!=null){
                                 objectMap=new HashMap<String, Object> ();
                                 for (SearchHitField filed : hit.fields().values()){
                                     objectMap.put( filed.name(),filed.getValue());
                                 }
                            }

                        }
                        if(objectMap!=null){

                         String title ="";
                         String summary ="";
                         String url = null;
                         Document doc = new Document();

                         if(req!=null){
                          if(req.UrlField!=null&&!req.UrlField.trim().isEmpty()){
                              if(objectMap.containsKey(req.UrlField.trim()))
                              {
                                  url= objectMap.get(req.UrlField.trim()).toString();
                                  doc.setField("_url", url);
                              }

                            }


                          if(req.TitleFields!=null&&req.TitleFields.length>0){
                             for (String title_field : req.TitleFields){
                                 if(objectMap.containsKey(title_field))
                                 {
                                     title = title +" "+ objectMap.get(title_field).toString();
                                 }
                              }
                          }else {
                              //TODO throw a exception
                          }

                          if(req.SummaryFields!=null&&req.SummaryFields.length>0){
                             for (String summary_field : req.SummaryFields){
                                 if(objectMap.containsKey(summary_field))
                                 {
                                     summary = summary +" "+ objectMap.get(summary_field).toString();
                                 }
                              }
                          }else {
                              //TODO throw a exception
                          }

                          if(req.Language!=null){
                              doc.setLanguage(LanguageCode.valueOf(req.Language.trim().toUpperCase()));
                          }
                        }

                        doc.setTitle(title);
                        doc.setSummary(summary);
                        doc.setField("_index", hit.index());
                        doc.setField("_type", hit.type());
                        doc.setField("_id", hit.id());

                        //if detail will attached
                        if(req.AttachDetail){
                          doc.setField("_title",title);
                          doc.setField("_summary",summary);
                          doc.setField("_url",url);
                        }
                        documents.add(doc);

                    }
                    }

                    final Map<String, Object> attributes = Maps.newHashMap();
                    LingoClusteringAlgorithmDescriptor
                    .attributeBuilder(attributes)
                    .desiredClusterCountBase(req.DesiredClusterCountBase)
                    .matrixReducer()
                    .factorizationQuality(IterationNumberGuesser.FactorizationQuality.HIGH);



                    MultilingualClusteringDescriptor.attributeBuilder(attributes)
                    .defaultLanguage(LanguageCode.ENGLISH);


                    File resourcesDir = new File(environment.configFile(), "carrot2/resources");

                    ResourceLookup resourceLookup = new ResourceLookup(new DirLocator(resourcesDir));

                    DefaultLexicalDataFactoryDescriptor.attributeBuilder(attributes)
                    .mergeResources(true);
                    LexicalDataLoaderDescriptor.attributeBuilder(attributes)
                    .resourceLookup(resourceLookup);

                    LingoClusteringAlgorithmDescriptor.AttributeBuilder builder = LingoClusteringAlgorithmDescriptor.attributeBuilder(attributes);
                    builder.matrixReducer().factorizationFactory(LocalNonnegativeMatrixFactorizationFactory.class);
                    builder.matrixBuilder().titleWordsBoost(7);
                    builder.clusterBuilder().phraseLabelBoost(req.PhraseLabelBoost);

                    CommonAttributesDescriptor.attributeBuilder(attributes)
                            .documents(documents);

                    processingResult = controller.process(
                            attributes, LingoClusteringAlgorithm.class);

                } catch (Exception e) {
                    logger.error("error", e);
                }

                try {
                    XContentBuilder builder = restContentBuilder(request);
                    builder.startObject();
                    //attach hits source
                    if(req.AttachSourceHits)
                    {response.toXContent(builder, request);}

                    if (processingResult != null && processingResult.getClusters() != null) {
                        final Collection<Cluster> clusters = processingResult.getClusters();
                        final Map<String, Object> attributes = processingResult.getAttributes();

                        builder.startObject("carrot2");
                        if (clusters != null && clusters.size() > 0) {
                            builder.startArray("clusters");
                            //displayClusters

                            //limit cluster
                            int return_cluster=0;
                            for (final Cluster cluster : clusters) {

                                if(return_cluster>req.MaxClusters){break;}
                                return_cluster+=1;

                                builder.startObject();
                                List<Document> docs = cluster.getAllDocuments();
                                int return_size=0;
                                if (docs != null && docs.size() > 0) {
                                    builder.field("size",docs.size());
                                    builder.field("name",cluster.getLabel());
                                    builder.startArray("documents");
                                    for (Document document : docs) {

                                        //do not return all of the docs
                                        if(return_size>req.MaxDocPerCluster){break;}
                                        return_size+=1;

                                        builder.startObject();
                                        builder.field("_index", document.getField("_index"));
                                        builder.field("_type", document.getField("_type"));
                                        builder.field("_id", document.getField("_id"));

                                        if(req!=null&&req.AttachDetail){
                                            builder.field("_title",document.getField("_title"));
                                            builder.field("_summary",document.getField("_summary"));
                                            builder.field("_url",document.getField("_url"));
                                        }
                                        builder.endObject();
                                    }
                                    builder.endArray();
                                }

                                List<String> phrases = cluster.getPhrases();
                                if (phrases != null && phrases.size() > 0) {
                                    builder.startArray("phrases");
                                    for (String str : phrases) {
                                        builder.value(str);
                                    }
                                    builder.endArray();
                                }

                                builder.endObject();
                            }
                            builder.endArray();
                        }


                        // Show attributes other attributes
                        String DOCUMENTS_ATTRIBUTE = CommonAttributesDescriptor.Keys.DOCUMENTS;
                        String CLUSTERS_ATTRIBUTE = CommonAttributesDescriptor.Keys.CLUSTERS;
                        for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
                            if (!DOCUMENTS_ATTRIBUTE.equals(attribute.getKey())
                                    && !CLUSTERS_ATTRIBUTE.equals(attribute.getKey())) {
                                builder.field(attribute.getKey(), attribute.getValue());
                            }

                        }
                        builder.endObject();
                    }
                    builder.endObject();
                    channel.sendResponse(new XContentRestResponse(request, response.status(), builder));
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("failed to execute search (building response)", e);
                    }
                    onFailure(e);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                try {
                    channel.sendResponse(new XContentThrowableRestResponse(request, e));
                } catch (IOException e1) {
                    logger.error("Failed to send failure response", e1);
                }
            }
        });
    }

    private Carrot2Request parseSearchRequest(RestRequest request) {
        String[] indices = RestActions.splitIndices(request.param("index"));
        Carrot2Request searchRequest = new Carrot2Request(indices);
        // get the content, and put it in the body
        if (request.hasContent()) {
            searchRequest.source(request.contentByteArray(), request.contentByteArrayOffset(), request.contentLength(), request.contentUnsafe());
        } else {
            String source = request.param("source");
            if (source != null) {
                searchRequest.source(source);
            }
        }

        // add extra source based on the request parameters
        searchRequest.extraSource(parseSearchSource(request));

        searchRequest.searchType(request.param("search_type"));

        String scroll = request.param("scroll");
        if (scroll != null) {
            searchRequest.scroll(new Scroll(parseTimeValue(scroll, null)));
        }

        searchRequest.types(RestActions.splitTypes(request.param("type")));
        searchRequest.queryHint(request.param("query_hint"));
        searchRequest.routing(request.param("routing"));
        searchRequest.preference(request.param("preference"));

        String param = request.param("carrot2.fetch_size");
        if (param != null) {
            searchRequest.FetchSize = Integer.parseInt(param);
        }

        param = request.param("carrot2.cluster_count_base");
        if (param != null) {
            searchRequest.DesiredClusterCountBase = Integer.parseInt(param);
        }


        searchRequest.Language = request.param("carrot2.language");
        searchRequest.Algorithm = request.param("carrot2.algorithm");
        searchRequest.UrlField = request.param("carrot2.url_field");
        if(request.param("carrot2.attach_detail")!=null)
        {
            searchRequest.AttachDetail = Boolean.parseBoolean(request.param("carrot2.attach_detail"));
        }
        if(request.param("carrot2.attach_hits")!=null)
        {
            searchRequest.AttachSourceHits = Boolean.parseBoolean(request.param("carrot2.attach_hits"));
        }
        if(request.param("carrot2.max_doc_per_cluster")!=null)
        {
            searchRequest.MaxDocPerCluster = Integer.parseInt(request.param("carrot2.max_doc_per_cluster"));
        }
        if(request.param("carrot2.max_cluster_size")!=null)
        {
            searchRequest.MaxClusters = Integer.parseInt(request.param("carrot2.max_cluster_size"));
        }

        param = request.param("carrot2.title_fields");
        if (param != null) {
            searchRequest.TitleFields = RestActions.splitTypes(param);
        }
        param = request.param("carrot2.summary_fields");
        if (param != null) {
            searchRequest.SummaryFields = RestActions.splitTypes(param);
        }
        return searchRequest;
    }

    private SearchSourceBuilder parseSearchSource(RestRequest request) {
        SearchSourceBuilder searchSourceBuilder = null;
        String queryString = request.param("q");
        if (queryString != null) {
            QueryStringQueryBuilder queryBuilder = QueryBuilders.queryString(queryString);
            queryBuilder.defaultField(request.param("df"));
            queryBuilder.analyzer(request.param("analyzer"));
            queryBuilder.analyzeWildcard(request.paramAsBoolean("analyze_wildcard", false));
            queryBuilder.lowercaseExpandedTerms(request.paramAsBoolean("lowercase_expanded_terms", true));
            String defaultOperator = request.param("default_operator");
            if (defaultOperator != null) {
                if ("OR".equals(defaultOperator)) {
                    queryBuilder.defaultOperator(QueryStringQueryBuilder.Operator.OR);
                } else if ("AND".equals(defaultOperator)) {
                    queryBuilder.defaultOperator(QueryStringQueryBuilder.Operator.AND);
                } else {
                    throw new ElasticSearchIllegalArgumentException("Unsupported defaultOperator [" + defaultOperator + "], can either be [OR] or [AND]");
                }
            }
            if (searchSourceBuilder == null) {
                searchSourceBuilder = new SearchSourceBuilder();
            }
            searchSourceBuilder.query(queryBuilder);
        }

        int from = request.paramAsInt("from", -1);
        if (from != -1) {
            if (searchSourceBuilder == null) {
                searchSourceBuilder = new SearchSourceBuilder();
            }
            searchSourceBuilder.from(from);
        }
        int size = request.paramAsInt("size", -1);
        if (size != -1) {
            if (searchSourceBuilder == null) {
                searchSourceBuilder = new SearchSourceBuilder();
            }
            searchSourceBuilder.size(size);
        }

        if (request.hasParam("explain")) {
            if (searchSourceBuilder == null) {
                searchSourceBuilder = new SearchSourceBuilder();
            }
            searchSourceBuilder.explain(request.paramAsBooleanOptional("explain", null));
        }
        if (request.hasParam("version")) {
            if (searchSourceBuilder == null) {
                searchSourceBuilder = new SearchSourceBuilder();
            }
            searchSourceBuilder.version(request.paramAsBooleanOptional("version", null));
        }
        if (request.hasParam("timeout")) {
            if (searchSourceBuilder == null) {
                searchSourceBuilder = new SearchSourceBuilder();
            }
            searchSourceBuilder.timeout(request.paramAsTime("timeout", null));
        }

        String sField = request.param("fields");
        if (sField != null) {
            if (searchSourceBuilder == null) {
                searchSourceBuilder = new SearchSourceBuilder();
            }
            if (!Strings.hasText(sField)) {
                searchSourceBuilder.noFields();
            } else {
                String[] sFields = Strings.splitStringByCommaToArray(sField);
                if (sFields != null) {
                    for (String field : sFields) {
                        searchSourceBuilder.field(field);
                    }
                }
            }
        }

        String sSorts = request.param("sort");
        if (sSorts != null) {
            if (searchSourceBuilder == null) {
                searchSourceBuilder = new SearchSourceBuilder();
            }
            String[] sorts = Strings.splitStringByCommaToArray(sSorts);
            for (String sort : sorts) {
                int delimiter = sort.lastIndexOf(":");
                if (delimiter != -1) {
                    String sortField = sort.substring(0, delimiter);
                    String reverse = sort.substring(delimiter + 1);
                    if ("asc".equals(reverse)) {
                        searchSourceBuilder.sort(sortField, SortOrder.ASC);
                    } else if ("desc".equals(reverse)) {
                        searchSourceBuilder.sort(sortField, SortOrder.DESC);
                    }
                } else {
                    searchSourceBuilder.sort(sort);
                }
            }
        }

        String sIndicesBoost = request.param("indices_boost");
        if (sIndicesBoost != null) {
            if (searchSourceBuilder == null) {
                searchSourceBuilder = new SearchSourceBuilder();
            }
            String[] indicesBoost = Strings.splitStringByCommaToArray(sIndicesBoost);
            for (String indexBoost : indicesBoost) {
                int divisor = indexBoost.indexOf(',');
                if (divisor == -1) {
                    throw new ElasticSearchIllegalArgumentException("Illegal index boost [" + indexBoost + "], no ','");
                }
                String indexName = indexBoost.substring(0, divisor);
                String sBoost = indexBoost.substring(divisor + 1);
                try {
                    searchSourceBuilder.indexBoost(indexName, Float.parseFloat(sBoost));
                } catch (NumberFormatException e) {
                    throw new ElasticSearchIllegalArgumentException("Illegal index boost [" + indexBoost + "], boost not a float number");
                }
            }
        }

        String sStats = request.param("stats");
        if (sStats != null) {
            if (searchSourceBuilder == null) {
                searchSourceBuilder = new SearchSourceBuilder();
            }
            searchSourceBuilder.stats(Strings.splitStringByCommaToArray(sStats));
        }

        return searchSourceBuilder;
    }
}
