package gr.hua.dit.studyrooms.web.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserTokenRequest(
        @NotNull @NotBlank String email,
        @NotNull @NotBlank String password
) {}
