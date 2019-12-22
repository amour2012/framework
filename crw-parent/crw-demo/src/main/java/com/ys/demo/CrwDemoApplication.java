package com.ys.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author oscar.wu
 *
 */
@SpringBootApplication
@ComponentScan(value={"com.ys.demo"})
public class CrwDemoApplication {
	public static void main(String[] args) {
        SpringApplication.run(CrwDemoApplication.class, args);
    }
}
