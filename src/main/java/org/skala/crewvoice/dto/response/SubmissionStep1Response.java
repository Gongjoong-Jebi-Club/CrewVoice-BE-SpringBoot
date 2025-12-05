package org.skala.crewvoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionStep1Response {
    private Long submissionId;

    public static SubmissionStep1Response of(Long submissionId) {
        return new SubmissionStep1Response(submissionId);
    }
}
