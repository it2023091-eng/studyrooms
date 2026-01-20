package gr.hua.dit.studyrooms.web.rest.model;

public record UserTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
