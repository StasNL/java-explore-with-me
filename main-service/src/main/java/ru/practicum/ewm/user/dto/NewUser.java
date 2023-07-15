package ru.practicum.ewm.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUser {
    @NotBlank
    @Size(min = 2, max = 250, message = "Слишком короткое или длинное имя пользователя")
    String name;
    @NotBlank
    @Email(regexp = "^[\\w-\\.]{1,63}@[\\w-\\.]{1,63}.[\\w-\\.]+$")
    @Size(min = 6, max = 254, message = "Слишком короткая или длинная электронная почта")
    String email;
}
