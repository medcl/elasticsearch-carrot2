import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.CommonAttributesDescriptor;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Medcl'
 * Date: 3/19/12
 * Time: 10:42 PM
 */
public class Test1 {
      public static void main(String[] args){

         /*
         * We use a Controller that reuse instances of Carrot2 processing components
         * and caches results produced by document sources.
         */
          final Controller controller = ControllerFactory.createCachingPooling(IDocumentSource.class);

          final List<Document> documents = Lists.newArrayList();

        /*sample data*/
        Document[] testData=new Document[]{
                new Document("hello world,python,java"),
                new Document("hello world,susan,python"),
                new Document("hello world,medcl,.net"),
                new Document( "world,michael,c++"),
                new Document("hello,jackson"),
                new Document("hello,c++,.net,java"),
        };
        for (Document document : testData)
        {
            documents.add(new Document(document.getTitle(), document.getSummary(),
                document.getContentUrl(), LanguageCode.ENGLISH));
        }

        final Map<String, Object> attributes = Maps.newHashMap();
        CommonAttributesDescriptor.attributeBuilder(attributes)
            .documents(documents);
        final ProcessingResult englishResult = controller.process(
            attributes, LingoClusteringAlgorithm.class);

        for (Cluster cluster:englishResult.getClusters()){
            System.out.println(cluster.getPhrases());
        }

    }
}
