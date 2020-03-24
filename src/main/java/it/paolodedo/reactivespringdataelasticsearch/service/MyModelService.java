package it.paolodedo.reactivespringdataelasticsearch.service;

import it.paolodedo.reactivespringdataelasticsearch.model.MyModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MyModelService {

    Mono<MyModel> findMyModelById(String id);

	Flux<MyModel> findAllMyModels(String field, String value);

	Mono<MyModel> saveMyModel(MyModel myModel);

	Mono<String> deleteMyModelById(String id);
}
