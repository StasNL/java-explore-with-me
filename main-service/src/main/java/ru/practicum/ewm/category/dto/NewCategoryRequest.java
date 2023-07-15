package ru.practicum.ewm.category.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryRequest {
    @NotBlank
    @Size(max = 50, message = "В названии категории должно быть не более 50 символов")
    String name;
}
