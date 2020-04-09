package com.usc;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Application <br/>
 * Class description: Application Entry. <br/>
 * date: 2019年7月31日 下午4:50:35 <br/>
 *
 * @author PuTianXiong
 * @since JDK 1.8
 */
@Slf4j
@EnableCaching
@EnableAsync
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class Application2_1_1 extends Application {

	public static void main(String[] args) {
		launch(args);
//		SpringApplication.run(Application2_1_1.class, args);
//		log.info("QMS-Service Started Successfully");
	}

	@Override
	public void start(Stage arg0) throws Exception {
		Stage stage = new Stage();
		Group group = new Group();
		stage.setTitle("Example");
		stage.setScene(new Scene(group, 300, 275));
		stage.show();

		Button button0 = new Button("button0");
		Button button1 = new Button("button1");
		group.getChildren().addAll(button0, button1);

		// 注意，高潮来了

		button0.layoutXProperty()
				.bind(stage.widthProperty().multiply(2).divide(3).subtract(button0.widthProperty().divide(2)));
		button0.layoutYProperty()
				.bind(stage.heightProperty().multiply(2).divide(3).subtract(button0.heightProperty().divide(2)));
		button0.prefWidthProperty().bind(stage.widthProperty().multiply(.1));

		button1.layoutXProperty()
				.bind(stage.widthProperty().multiply(1).divide(3).subtract(button1.widthProperty().divide(2)));
		button1.layoutYProperty()
				.bind(stage.heightProperty().multiply(2).divide(3).subtract(button1.heightProperty().divide(2)));
		button1.prefWidthProperty().bind(stage.widthProperty().multiply(.1));
	}

}
