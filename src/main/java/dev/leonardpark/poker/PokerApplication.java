package dev.leonardpark.poker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PokerApplication {
  private static final Logger log = LoggerFactory.getLogger(PokerApplication.class);

  static int staticServerPort = 0;
  static String staticServerAddress = "";

  public static void main(String[] args) {
    SpringApplication.run(PokerApplication.class, args);
  }

  @Value("${server.port}")
  public void setStaticServerPort(int port) {
    PokerApplication.staticServerPort = port;
  }

  @Value("${server.address}")
  public void setStaticServerAddress(String address) {
    PokerApplication.staticServerAddress = address;
  }

  @Bean
  CommandLineRunner run() {
    return args -> {
      log.info(String.format("%s://%s:%s", "http", staticServerAddress, staticServerPort));
    };
  }
}
