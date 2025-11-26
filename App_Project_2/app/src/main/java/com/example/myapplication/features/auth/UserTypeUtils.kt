package com.example.myapplication.features.auth

object UserTypeUtils {
    private val employerKeywords = setOf(
        "employer",
        "employee",
        "employer account",
        "employers",
        "employer/employee",
        "employee/employer",
        "employer profile",
        "company",
        "business",
        "recruiter",
        "hirer"
    )

    private val studentKeywords = setOf(
        "student",
        "student/job seeker",
        "student job seeker",
        "job seeker",
        "candidate",
        "applicant",
        "student account",
        "student/jobseeker"
    )

    /**
     * Normalizes the raw userType string into the canonical values used across the UI.
     */
    fun normalize(rawValue: String?): String? {
        val value = rawValue?.trim().orEmpty()
        if (value.isEmpty()) return null

        val lowered = value.lowercase()
        return when {
            employerKeywords.any { lowered == it } -> "Employer"
            studentKeywords.any { lowered == it } -> "Student/Job Seeker"
            else -> value
        }
    }

    fun isEmployer(rawValue: String?): Boolean = normalize(rawValue) == "Employer"

    fun isStudent(rawValue: String?): Boolean = normalize(rawValue) == "Student/Job Seeker"
}

