package it.paolodedo.reactivespringdataelasticsearch.service.impl;

import it.paolodedo.reactivespringdataelasticsearch.model.MyModel;
import it.paolodedo.reactivespringdataelasticsearch.service.MyModelService;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import static it.paolodedo.reactivespringdataelasticsearch.util.Constants.DEFAULT_ES_DOC_TYPE;
import static it.paolodedo.reactivespringdataelasticsearch.util.Constants.MYMODEL_ES_INDEX;

@Service
@EnableScheduling
public class MyModelServiceImpl implements MyModelService {

    @PostConstruct
    private void checkIndexExists(){

        GetIndexRequest request = new GetIndexRequest();
        request.indices(MYMODEL_ES_INDEX);

        reactiveElasticsearchClient.indices()
            .existsIndex(request)
            .doOnError(throwable -> logger.error(throwable.getMessage(), throwable))
            .flatMap(indexExists -> {
                logger.info("Index {} exists: {}", MYMODEL_ES_INDEX, indexExists);
                if (!indexExists)
                    return createIndex();
                else
                    return Mono.empty();
            })
            .subscribe();
    }

    private Mono<Void> createIndex(){

        CreateIndexRequest request = new CreateIndexRequest();
        request.index(MYMODEL_ES_INDEX);
        request.mapping(DEFAULT_ES_DOC_TYPE,
        "{\n" +
                "  \"properties\": {\n" +
                "    \"timestamp\": {\n" +
                "      \"type\": \"date\",\n" +
                "      \"format\": \"epoch_millis||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd\"\n" +
                "    }\n" +
                "  }\n" +
                "}",
            XContentType.JSON);

        return reactiveElasticsearchClient.indices()
            .createIndex(request)
            .doOnSuccess(aVoid -> logger.info("Created Index {}", MYMODEL_ES_INDEX))
            .doOnError(throwable -> logger.error(throwable.getMessage(), throwable));
    }

    @Override
    public Mono<MyModel> findMyModelById(String id){
        
        return reactiveElasticsearchOperations.findById(
            id,
            MyModel.class,
            MYMODEL_ES_INDEX,
            DEFAULT_ES_DOC_TYPE
        ).doOnError(throwable -> logger.error(throwable.getMessage(), throwable));
    }

    @Override
    public Mono<MyModel> saveMyModel(MyModel myModel){

        return reactiveElasticsearchOperations.save(
            myModel,
            MYMODEL_ES_INDEX,
            DEFAULT_ES_DOC_TYPE
        ).doOnError(throwable -> logger.error(throwable.getMessage(), throwable));
    }

    private static final Logger logger = LoggerFactory.getLogger(MyModelServiceImpl.class);

    private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;

    private final ReactiveElasticsearchClient reactiveElasticsearchClient;

    public MyModelServiceImpl(ReactiveElasticsearchOperations reactiveElasticsearchOperations,
                              ReactiveElasticsearchClient reactiveElasticsearchClient) {
        this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
        this.reactiveElasticsearchClient = reactiveElasticsearchClient;
    }
}
