package org.example.bddconcurrentaccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/counters")
public class CounterController {

    @Autowired
    private CounterService counterService;

    @GetMapping("/{counterId}")
    public int getCounterValue(@PathVariable String counterId) {
        return counterService.getCounterValue(counterId);
    }

    @PostMapping("/{counterId}")
    public int incrementCounter(@PathVariable String counterId, @RequestBody int incrementValue) {
        return counterService.incrementCounter(counterId, incrementValue);
    }
}
