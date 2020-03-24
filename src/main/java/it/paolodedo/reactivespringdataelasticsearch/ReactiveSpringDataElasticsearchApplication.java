package it.paolodedo.reactivespringdataelasticsearch;

import it.paolodedo.reactivespringdataelasticsearch.model.MyModel;
import it.paolodedo.reactivespringdataelasticsearch.service.MyModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
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
		Mono<MyModel> saveMono = myModelService.saveMyModel(myModel);

		saveMono.subscribe(savedObject -> {
			logger.info("Object persisted: {}", savedObject);

			// UPDATE
			myModel.setData("test UPDATED");
			myModel.setTimestamp(new Date().getTime());
			myModel.setId(savedObject.getId());

			myModelService.saveMyModel(myModel)
				.subscribe(updatedObject -> {
					logger.info("Object updated: {}", updatedObject);

					// FIND ALL FILTERED BY FIELD
					myModelService.findAllMyModels("data", "test UPDATED")
						.subscribe(findAllResult -> logger.info("FindAll result: {}", findAllResult));

					// FIND BY ID
					myModelService.findMyModelById(updatedObject.getId())
						.subscribe(foundObject -> {
							logger.info("Object found: {}", foundObject);

							// DELETE
							myModelService.deleteMyModelById(foundObject.getId())
								.subscribe(deletedObjectId -> {
									logger.info("Object deleted's ID: {}", deletedObjectId);
								});
						});
				});
		});
	}


}
