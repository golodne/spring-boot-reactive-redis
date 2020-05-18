package ru.reactiveredis.springbootreactiveredis.util;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.reactiveredis.springbootreactiveredis.model.Coffee;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class CofeePreLoader {
    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, Coffee> coffeeOps;

    public CofeePreLoader(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, Coffee> coffeeOps) {
        this.factory = factory;
        this.coffeeOps = coffeeOps;
    }

    @PostConstruct
    public void loadData() {
        factory.getReactiveConnection().serverCommands().flushAll().thenMany(
                Flux.just("Jet Black Redis", "Darth Redis", "Black Alert Redis")
                        .map(name -> new Coffee(UUID.randomUUID().toString(), name))
                        .flatMap(coffee -> coffeeOps.opsForValue().set(coffee.getId(), coffee))
        ).thenMany(coffeeOps.keys("*")
                .flatMap(coffeeOps.opsForValue()::get))
                .subscribe(System.out::println);
    }
}
