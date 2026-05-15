package com.example.sell_lp.component;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TransactionCodeGenerator {

    // Bộ đếm an toàn luồng, giới hạn tuần hoàn từ 1000 đến 9999 để giữ độ dài mã cố định
    private final AtomicLong counter = new AtomicLong(1000);

    public String generate() {
        // Lấy thời gian hiện tại theo định dạng Unix Timestamp (giây) - ví dụ: 1715817600
        long timestamp = Instant.now().getEpochSecond();

        // Tăng bộ đếm, nếu vượt quá 9999 thì reset về 1000 để tránh mã quá dài
        long sequence = counter.getAndIncrement();
        if (sequence > 9999) {
            counter.set(1000);
            sequence = 1000;
        }

        // Kết quả dạng: LAP17158176001001 (Độ dài cố định, không trùng lặp)
        return String.format("LAP%d%d", timestamp, sequence);
    }
}