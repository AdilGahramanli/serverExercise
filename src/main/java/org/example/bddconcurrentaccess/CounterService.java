package org.example.bddconcurrentaccess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CounterService {

    @Autowired
    private CounterRepository counterRepository;

    public int getCounterValue(String counterId) {
        Optional<Counter> counter = counterRepository.findById(counterId);
        return counter.map(Counter::getValue).orElse(0);
    }

    public synchronized int incrementCounter(String counterId, int incrementValue) {
        Counter counter = counterRepository.findById(counterId).orElse(new Counter());
        int currentValue = counter.getValue();
        int newValue = currentValue + incrementValue;

        // Simuler un traitement long
        try {
            Thread.sleep(200); // pause de 200 ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        counter.setValue(newValue);
        counterRepository.save(counter);
        return newValue;
    }
}
