package ru.reactiveredis.springbootreactiveredis.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.reactiveredis.springbootreactiveredis.model.Coffee;

@RestController
@Slf4j
public class CofeeController {

    private final ReactiveRedisOperations<String, Coffee> coffeeOps;

   // @Autowired
   // @Qualifier("redisTmp")
  //  private ReactiveRedisTemplate<String, Coffee> reactiveTemplate;

    @Autowired
    private ReactiveRedisMessageListenerContainer reactiveMsgListenerContainer;

    @Autowired
    private ChannelTopic topic;

    CofeeController(ReactiveRedisOperations<String, Coffee> coffeeOps) {
        this.coffeeOps = coffeeOps;
    }

    @GetMapping("/coffee/all")
    public Flux<Coffee> getAll(){
        log.info("Receiving all Coffees from Redis.");
        return coffeeOps.keys("*")
                        .flatMap(coffeeOps.opsForValue()::get);
    }

    @GetMapping("/coffee/{id}")
    //http://localhost:8080/coffee/4f03c780-6606-400c-8fc9-d772654896b1
    public Mono<Coffee> getOne(@PathVariable("id") String id) {
        log.info("Receiving one Coffee from Redis.");
        return coffeeOps.opsForValue().get(id);
    }
/*
    @PostMapping("/message/coffee/{variety}")
    public Mono<Long> sendCoffeeMessage(@PathVariable String variety) {
        log.info("New Coffee with variety '" + variety + "' send to Channel '" + topic.getTopic() + "'.");
        return reactiveTemplate.convertAndSend(topic.getTopic(), variety);
    }
*/
    @PostMapping("/coffee/add")
    public Mono<Boolean> addCoffee(@RequestBody Coffee request) {
        log.info("add topic " + request);
        return coffeeOps.opsForValue().set(request.getId(),request);
    }
}