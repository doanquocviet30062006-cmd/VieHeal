package com.clinic.platform.modules.iam.domain

import java.util.UUID

interface IamRoleRepository {

    fun findById(id: UUID): IamRole?

    fun findByCode(code: String): IamRole?
}