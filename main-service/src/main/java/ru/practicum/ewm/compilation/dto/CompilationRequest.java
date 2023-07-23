package ru.practicum.ewm.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationRequest {
    @Positive
    Long id;
    @Size(max = 50, message = "В названии подборки допустимо не более 50 символов.")
    String title;
    List<Long> events;
    Boolean pinned;
}
