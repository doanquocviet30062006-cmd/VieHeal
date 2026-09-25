package com.clinic.platform.modules.iam.api

import com.clinic.platform.infrastructure.security.AccessAuthorizationService
import com.clinic.platform.modules.iam.api.dto.IamUserResponse
import com.clinic.platform.modules.iam.application.GetIamUserUseCase
import com.clinic.platform.shared.security.PermissionCodes
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/iam/users")
class IamUserController(
    private val getIamUserUseCase: GetIamUserUseCase,
    private val accessAuthorizationService:
        AccessAuthorizationService
) {

    @GetMapping("/{id}")
    fun getById(
        @PathVariable
        id: UUID,

        @AuthenticationPrincipal
        jwt: Jwt
    ): ResponseEntity<IamUserResponse> {

        accessAuthorizationService
            .requireUserPermission(
                jwt = jwt,
                targetUserId = id,
                permission = PermissionCodes.IAM_USER_READ
            )

        val user =
            getIamUserUseCase.execute(id)

        return ResponseEntity.ok(
            IamUserResponse.fromDomain(user)
        )
    }
}