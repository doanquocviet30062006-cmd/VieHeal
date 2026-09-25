package com.clinic.platform.modules.patient.domain

import com.clinic.platform.shared.errors.DomainValidationException
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class Patient(
    val id: UUID,
    val organizationId: UUID,
    val managingFacilityId: UUID,
    val patientCode: String,
    val fullName: String,
    val dateOfBirth: LocalDate?,
    val sex: PatientSex,
    val phone: String?,
    val email: String?,
    val addressLine: String?,
    val ward: String?,
    val district: String?,
    val province: String?,
    val countryCode: String,
    val status: PatientStatus,
    val createdByUserId: UUID,
    val updatedByUserId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {

        fun create(
            organizationId: UUID,
            managingFacilityId: UUID,
            patientCode: String,
            fullName: String,
            dateOfBirth: LocalDate?,
            sex: PatientSex,
            phone: String?,
            email: String?,
            addressLine: String?,
            ward: String?,
            district: String?,
            province: String?,
            countryCode: String,
            actorUserId: UUID
        ): Patient {

            val normalizedPatientCode =
                patientCode
                    .trim()
                    .uppercase()

            val normalizedFullName =
                fullName.trim()

            val normalizedPhone =
                normalizeOptional(phone)

            val normalizedEmail =
                normalizeOptional(email)
                    ?.lowercase()

            val normalizedAddressLine =
                normalizeOptional(addressLine)

            val normalizedWard =
                normalizeOptional(ward)

            val normalizedDistrict =
                normalizeOptional(district)

            val normalizedProvince =
                normalizeOptional(province)

            val normalizedCountryCode =
                countryCode
                    .trim()
                    .uppercase()

            validatePatientCode(
                normalizedPatientCode
            )

            validateFullName(
                normalizedFullName
            )

            validateDateOfBirth(
                dateOfBirth
            )

            validateCountryCode(
                normalizedCountryCode
            )

            validateOptionalFieldLengths(
                phone = normalizedPhone,
                email = normalizedEmail,
                addressLine = normalizedAddressLine,
                ward = normalizedWard,
                district = normalizedDistrict,
                province = normalizedProvince
            )

            val now = Instant.now()

            return Patient(
                id = UUID.randomUUID(),
                organizationId = organizationId,
                managingFacilityId = managingFacilityId,
                patientCode = normalizedPatientCode,
                fullName = normalizedFullName,
                dateOfBirth = dateOfBirth,
                sex = sex,
                phone = normalizedPhone,
                email = normalizedEmail,
                addressLine = normalizedAddressLine,
                ward = normalizedWard,
                district = normalizedDistrict,
                province = normalizedProvince,
                countryCode = normalizedCountryCode,
                status = PatientStatus.ACTIVE,

                createdByUserId = actorUserId,
                updatedByUserId = actorUserId,

                createdAt = now,
                updatedAt = now
            )
        }

        private fun normalizeOptional(
            value: String?
        ): String? =
            value
                ?.trim()
                ?.takeIf { it.isNotBlank() }

        private fun validatePatientCode(
            patientCode: String
        ) {
            if (patientCode.isBlank()) {
                throw DomainValidationException(
                    message =
                        "Patient code must not be blank",
                    code =
                        "PATIENT_CODE_BLANK"
                )
            }

            if (patientCode.length > 50) {
                throw DomainValidationException(
                    message =
                        "Patient code must not exceed 50 characters",
                    code =
                        "PATIENT_CODE_TOO_LONG"
                )
            }
        }

        private fun validateFullName(
            fullName: String
        ) {
            if (fullName.isBlank()) {
                throw DomainValidationException(
                    message =
                        "Patient full name must not be blank",
                    code =
                        "PATIENT_FULL_NAME_BLANK"
                )
            }

            if (fullName.length > 255) {
                throw DomainValidationException(
                    message =
                        "Patient full name must not exceed 255 characters",
                    code =
                        "PATIENT_FULL_NAME_TOO_LONG"
                )
            }
        }

        private fun validateDateOfBirth(
            dateOfBirth: LocalDate?
        ) {
            if (
                dateOfBirth != null &&
                dateOfBirth.isAfter(LocalDate.now())
            ) {
                throw DomainValidationException(
                    message =
                        "Patient date of birth must not be in the future",
                    code =
                        "PATIENT_DATE_OF_BIRTH_IN_FUTURE"
                )
            }
        }

        private fun validateCountryCode(
            countryCode: String
        ) {
            if (
                !countryCode.matches(
                    Regex("^[A-Z]{2}$")
                )
            ) {
                throw DomainValidationException(
                    message =
                        "Patient country code must contain exactly 2 uppercase letters",
                    code =
                        "PATIENT_COUNTRY_CODE_INVALID"
                )
            }
        }

        private fun validateOptionalFieldLengths(
            phone: String?,
            email: String?,
            addressLine: String?,
            ward: String?,
            district: String?,
            province: String?
        ) {
            if (
                phone != null &&
                phone.length > 30
            ) {
                throw DomainValidationException(
                    message =
                        "Patient phone must not exceed 30 characters",
                    code =
                        "PATIENT_PHONE_TOO_LONG"
                )
            }

            if (
                email != null &&
                email.length > 255
            ) {
                throw DomainValidationException(
                    message =
                        "Patient email must not exceed 255 characters",
                    code =
                        "PATIENT_EMAIL_TOO_LONG"
                )
            }

            if (
                addressLine != null &&
                addressLine.length > 255
            ) {
                throw DomainValidationException(
                    message =
                        "Patient address line must not exceed 255 characters",
                    code =
                        "PATIENT_ADDRESS_TOO_LONG"
                )
            }

            if (
                ward != null &&
                ward.length > 100
            ) {
                throw DomainValidationException(
                    message =
                        "Patient ward must not exceed 100 characters",
                    code =
                        "PATIENT_WARD_TOO_LONG"
                )
            }

            if (
                district != null &&
                district.length > 100
            ) {
                throw DomainValidationException(
                    message =
                        "Patient district must not exceed 100 characters",
                    code =
                        "PATIENT_DISTRICT_TOO_LONG"
                )
            }

            if (
                province != null &&
                province.length > 100
            ) {
                throw DomainValidationException(
                    message =
                        "Patient province must not exceed 100 characters",
                    code =
                        "PATIENT_PROVINCE_TOO_LONG"
                )
            }
        }
    }

    fun updateProfile(
        fullName: String,
        dateOfBirth: LocalDate?,
        sex: PatientSex,
        phone: String?,
        email: String?,
        addressLine: String?,
        ward: String?,
        district: String?,
        province: String?,
        countryCode: String,
        actorUserId: UUID
    ): Patient {

        val normalizedFullName =
            fullName.trim()

        val normalizedPhone =
            normalizeOptional(phone)

        val normalizedEmail =
            normalizeOptional(email)
                ?.lowercase()

        val normalizedAddressLine =
            normalizeOptional(addressLine)

        val normalizedWard =
            normalizeOptional(ward)

        val normalizedDistrict =
            normalizeOptional(district)

        val normalizedProvince =
            normalizeOptional(province)

        val normalizedCountryCode =
            countryCode
                .trim()
                .uppercase()

        validateFullName(
            normalizedFullName
        )

        validateDateOfBirth(
            dateOfBirth
        )

        validateCountryCode(
            normalizedCountryCode
        )

        validateOptionalFieldLengths(
            phone = normalizedPhone,
            email = normalizedEmail,
            addressLine = normalizedAddressLine,
            ward = normalizedWard,
            district = normalizedDistrict,
            province = normalizedProvince
        )

        return copy(
            fullName = normalizedFullName,
            dateOfBirth = dateOfBirth,
            sex = sex,
            phone = normalizedPhone,
            email = normalizedEmail,
            addressLine = normalizedAddressLine,
            ward = normalizedWard,
            district = normalizedDistrict,
            province = normalizedProvince,
            countryCode = normalizedCountryCode,

            updatedByUserId = actorUserId,
            updatedAt = Instant.now()
        )
    }
}