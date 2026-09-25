package com.clinic.platform.infrastructure.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain {

        http
            .csrf {
                it.disable()
            }

            .sessionManagement {
                it.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }

            .authorizeHttpRequests {
                it

                    // PUBLIC
                    .requestMatchers(
                        "/api/v1/health",
                        "/actuator/health"
                    )
                    .permitAll()

                    // ORGANIZATION
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/organizations/*"
                    )
                    .authenticated()

                    // FACILITY LIST
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/organizations/*/facilities"
                    )
                    .authenticated()

                    // FACILITY CREATE
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/organizations/*/facilities"
                    )
                    .authenticated()

                    // FACILITY GET
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/facilities/*"
                    )
                    .authenticated()

                    // ORGANIZATION MEMBERS LIST
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/organizations/*/members"
                    )
                    .authenticated()

                    // ORGANIZATION MEMBER CREATE
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/organizations/*/members"
                    )
                    .authenticated()

                    // ORGANIZATION USER PROVISION
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/organizations/*/users"
                    )
                    .authenticated()

                    // IAM USER GET
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/iam/users/*"
                    )
                    .authenticated()

                    // MEMBERSHIP ROLES
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/memberships/*/roles"
                    )
                    .authenticated()

                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/memberships/*/roles"
                    )
                    .authenticated()

                    // MEMBERSHIP FACILITIES
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/memberships/*/facilities"
                    )
                    .authenticated()

                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/memberships/*/facilities"
                    )
                    .authenticated()

                    // TEMPORARY:
                    // global organization creation vẫn mở
                    // cho tới khi SYSTEM_ADMIN được model hoàn chỉnh.
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/organizations"
                    )
                    .permitAll()

                    // Mọi endpoint còn lại phải authenticated
                    .anyRequest()
                    .authenticated()
            }

            .oauth2ResourceServer {
                it.jwt { }
            }

        return http.build()
    }
}