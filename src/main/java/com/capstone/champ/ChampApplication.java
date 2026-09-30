package com.capstone.champ;

import com.capstone.champ.configuration.MlServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MlServiceProperties.class)
public class ChampApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChampApplication.class, args);
	}

}
