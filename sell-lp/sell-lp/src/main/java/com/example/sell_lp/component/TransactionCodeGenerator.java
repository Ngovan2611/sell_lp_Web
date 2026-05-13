package com.example.sell_lp.component;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TransactionCodeGenerator {

    private final AtomicLong counter = new AtomicLong(1);

    public String generate() {

        String date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        long sequence = counter.getAndIncrement();

        return String.format(
                "LAP%s%04d",
                date,
                sequence
        );
    }
}