package com.core.arnuv.request;

import com.core.arnuv.model.Usuariodetalle;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String password;
    private String confirmPassword;
    private Usuariodetalle user;
}
