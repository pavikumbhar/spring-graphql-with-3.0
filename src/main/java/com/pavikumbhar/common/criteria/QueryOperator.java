package com.pavikumbhar.common.criteria;

public enum QueryOperator {
    GREATER_THAN,
    LESS_THAN,
    EQUALS,
    LIKE,
    NOT_EQ,

    IN,
    NOT_EQUAL,
    STARTS_WITH,
    ENDS_WITH,
    CONTAINS,
    BETWEEN,
    IS_NULL,
    IS_NOT_NULL,
    IS_EMPTY,
    IS_NOT_EMPTY,
    MEMBER_OF, // for collections
    NOT_MEMBER_OF, // for collections
    EXISTS, // for subqueries
    NOT_EXISTS, // for subqueries
    GREATER_THAN_OR_EQUAL,
    LESS_THAN_OR_EQUAL,
    NOT_LIKE,
    MATCHES_REGEX,
    IS_TRUE,
    IS_FALSE,
    IGNORE_CASE,
    AFTER, // Date-based operations
    BEFORE, // Date-based operations
    NULL_SAFE_EQUAL, // Null-safe equality comparison
    TRUE_OR_NULL, // True or Null comparison
    FALSE_OR_NULL, // False or Null comparison
    NOT_EQUAL_IGNORE_CASE,
    IS_BETWEEN_NULL_SAFE,
    IS_INSENSITIVE_LIKE,
    IS_EMPTY_OR_NULL, // Check if empty or null for strings or collections
    IS_NOT_EMPTY_OR_NULL,
    IN_IGNORE_CASE,
    STARTS_WITH_IGNORE_CASE,
    ENDS_WITH_IGNORE_CASE,
    CONTAINS_IGNORE_CASE,
    NOT_LIKE_IGNORE_CASE,
    IS_MEMBER_IGNORE_CASE, // Case-insensitive IS_MEMBER for collections
    NOT_MEMBER_IGNORE_CASE, // Case-insensitive NOT_MEMBER for collections
    IS_UPPERCASE, // Check if value is in uppercase
    IS_LOWERCASE, // Check if value is in lowercase
    IS_EMAIL, // Check if value is a valid email address
    NOT_EQUALS, IS_URL // Check if value is a valid URL
    // Add more operations as needed
}
