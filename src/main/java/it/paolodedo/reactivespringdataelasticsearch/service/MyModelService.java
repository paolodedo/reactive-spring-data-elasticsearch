package it.paolodedo.reactivespringdataelasticsearch.service;

import it.paolodedo.reactivespringdataelasticsearch.model.MyModel;
import reactor.core.publisher.Mono;

public interface MyModelService {

    Mono<MyModel> findMyModelById(String id);

    Mono<MyModel> saveMyModel(MyModel myModel);

}
