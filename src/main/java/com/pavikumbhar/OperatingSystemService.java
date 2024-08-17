package com.pavikumbhar;

import com.pavikumbhar.entity.OperatingSystem;
import com.pavikumbhar.repository.OperatingSystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatingSystemService {

    private final OperatingSystemRepository operatingSystemRepository;
    public PaginatedItems getOperatingSystems(int limit, String cursor) {
        Pageable pageable = PageRequest.of(0, limit);  // `first` is the limit here
        Long id=null;

        if (cursor != null && !cursor.isEmpty()) {
            String decodedCursor = decodeCursor(cursor);
            id= Long.valueOf(decodedCursor);

        }
        List<OperatingSystem> items = operatingSystemRepository.findOperatingSystems(id, pageable);
        String nextCursor = calculateNextCursor(items);
        int totalElements = operatingSystemRepository.countTotalOperatingSystems();

        return new PaginatedItems(items, nextCursor, totalElements);
    }

    private String calculateNextCursor(List<OperatingSystem> items) {
        if (items.isEmpty()) {
            return null;
        }
        // Return the encoded ID of the last item
        return encodeCursor(String.valueOf(items.get(items.size() - 1).getId()));
    }

    public String encodeCursor(String itemId) {
        return Base64.getEncoder().encodeToString(itemId.getBytes());
    }

    public String decodeCursor(String encodedCursor) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedCursor);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid cursor format", e);
        }
    }
}

