package org.example.VKR;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class VKRApplication {

	public static void main(String[] args) {
		SpringApplication.run(VKRApplication.class, args);
			}

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
}
