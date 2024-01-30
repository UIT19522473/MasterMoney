package com.project.app.exceptions;

import com.project.app.response.CommonResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThrowException extends RuntimeException{
    private CommonResponse commonResponse;
}
