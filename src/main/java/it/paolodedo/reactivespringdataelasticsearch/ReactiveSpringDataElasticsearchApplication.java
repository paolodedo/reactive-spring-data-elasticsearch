package it.paolodedo.reactivespringdataelasticsearch;

import it.paolodedo.reactivespringdataelasticsearch.model.MyModel;
import it.paolodedo.reactivespringdataelasticsearch.service.MyModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Date;

@SpringBootApplication
public class ReactiveSpringDataElasticsearchApplication {

	private static final Logger logger = LoggerFactory.getLogger(ReactiveSpringDataElasticsearchApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReactiveSpringDataElasticsearchApplication.class, args);
	}

	private final MyModelService myModelService;

	public ReactiveSpringDataElasticsearchApplication(MyModelService myModelService) {
		this.myModelService = myModelService;
	}

	@PostConstruct
	public void test() {

		// SAVE
		MyModel myModel = new MyModel();
		myModel.setData("test");
		myModel.setTimestamp(new Date().getTime());

		myModelService.saveMyModel(myModel)
			.doOnNext(savedObject -> logger.info("Object persisted: {}", savedObject))
			.delayElement(Duration.ofSeconds(2))
			.flatMapMany(savedObject -> {
				// FIND ALL FILTERED BY FIELD
				return myModelService.findAllMyModels("data", "test");
			})
			.delayElements(Duration.ofSeconds(2))
			.doOnNext(objectRetrieved -> logger.info("Objects retrieved by find all: {}", objectRetrieved))
			.flatMap(objectRetrieved -> {
				// UPDATE
				objectRetrieved.setData("test UPDATED");
				objectRetrieved.setTimestamp(new Date().getTime());
				return myModelService.saveMyModel(objectRetrieved);
			})
			.delayElements(Duration.ofSeconds(2))
			.doOnNext(updatedObject -> logger.info("Object updated: {}", updatedObject))
			.flatMap(updatedObject -> {
				// FIND BY ID
				return myModelService.findMyModelById(updatedObject.getId());
			})
			.delayElements(Duration.ofSeconds(2))
			.doOnNext(foundObject -> logger.info("Object found: {}", foundObject))
			.flatMap(foundObject -> {
				// DELETE
				return myModelService.deleteMyModelById(foundObject.getId());
			})
			.delayElements(Duration.ofSeconds(2))
			.doOnNext(deletedObjectId -> logger.info("Object deleted's ID: {}", deletedObjectId))
			.subscribe();
	}

}
