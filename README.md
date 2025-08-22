# Portfolio-RestfulBooker API Test Framework

[![Java](https://img.shields.io/badge/Java-17%2B-blue)](https://java.com)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-orange)](https://maven.apache.org)
[![TestNG](https://img.shields.io/badge/TestNG-7.8%2B-red)](https://testng.org)
[![RestAssured](https://img.shields.io/badge/RestAssured-5.3%2B-green)](https://rest-assured.io)
[![Retry](https://img.shields.io/badge/Retry%20Logic-Enabled-brightgreen)](https://github.com/jovana-st/Portfolio-RestfulBooker)

## 📖 Overview

A comprehensive test automation framework for the [Restful-Booker API](https://restful-booker.herokuapp.com/apidoc/index.html) demonstrating modern API testing practices with RestAssured, TestNG, and Maven. Features intelligent retry logic for handling flaky API operations.

## 🏗️ Architecture
```text
src/
├── main/
│   ├── java/
│   │   ├── config/       # Configuration classes
│   │   ├── models/       # Data transfer objects
│   │   ├── services/     # API service layers
│   │   ├── specs/        # Request specifications
│   │   ├── utils/        # Utility classes
│   │   └── listeners/    # TestNG listeners
│   └── resources/
│       ├── schemas/      # JSON schema definitions
│       └── testng.xml    # Test configuration
└── test/
    └── java/
        └── tests/        # Test implementations
```

## 🛠️ Technology Stack

- **Java 17+**: Core programming language
- **RestAssured**: API testing and validation
- **TestNG**: Test framework with parallel execution and retry capabilities
- **Maven**: Dependency management and build tool
- **Lombok**: Reduced boilerplate code
- **Gson**: JSON serialization/deserialization
- **JavaFaker**: Test data generation

## ⚡ Smart Retry System

The framework includes an intelligent retry mechanism that automatically handles flaky API operations:

### 🔧 Retry Configuration
- **Max Attempts**: 3 retries per test
- **Initial Delay**: 500ms between retries
- **Exponential Backoff**: Delay increases with each retry
- **Smart Filtering**: Skips retries for error condition tests

### 🎯 How It Works
1. **Automatic Retry**: All tests have retry enabled by default via TestNG listeners
2. **Smart Skipping**: Error condition tests (4xx/5xx validation) are excluded from retries
3. **Centralized Control**: Retry behavior configured in one place (`GlobalRetryAnalyzer`)
4. **Opt-Out Option**: Use `@NoRetry` annotation for specific tests

### 📋 Retry Rules
| Test Type | Retry Behavior | Examples |
|-----------|----------------|----------|
| **API Operations** | ✅ Retry enabled | `createBooking()`, `getBookingIds()` |
| **Authentication** | ✅ Retry enabled | `authWithValidCredentials()` |
| **Error Conditions** | ❌ No retry | `deleteBookingWrongAuth()`, tests expecting 4xx/5xx |
| **Edge Cases** | ✅ Retry enabled | Data-driven tests, boundary tests |

## 📦 Installation

1. **Prerequisites**:
   - Java 8+
   - Maven 3.8+
   - TestNG (configured in `pom.xml`)

2. **Run Tests**:
   ```bash
   mvn clean test

## 🔧 Configuration

### Retry Configuration
Retry behavior is configured in `GlobalRetryAnalyzer.java`:

```java
private static final int MAX_RETRY_COUNT = 3;          // Number of retry attempts
private static final long INITIAL_DELAY_MS = 500;      // Initial delay in milliseconds  
private static final double BACKOFF_MULTIPLIER = 2.0;  // Delay multiplier for exponential backoff
```

## 🧪 Test Coverage
- ✅ Authentication tests with token caching and retry logic
- ✅ CRUD operations for bookings with automatic retry handling
- ✅ Data validation with JSON schemas
- ✅ Edge case testing with data-driven tests
- ✅ Error handling scenarios (no retry for error conditions)
- ✅ Performance validation with response time assertions

## 🚀 Key Features
- **Modular Architecture**: Clean separation of concerns
- **Smart Retry Logic**: Automatic handling of flaky API operations  
- **Data-Driven Testing**: Support for multiple test data scenarios
- **Schema Validation**: JSON schema validation for all responses
- **Soft Assertions**: Comprehensive validation without early test failure
- **Parallel Execution**: TestNG configuration for parallel test runs
- **Centralized Error Handling**: Consistent exception management across services

## 📝 License
This project is for portfolio purposes. Please refer to the Restful-Booker API terms for usage guidelines.

## ⚠️ Note
This framework tests against a mock API implementation. Some test cases may reflect mocked behavior rather than actual API responses. The retry system is designed to handle transient failures in real API environments.

## 🔗 Links
- [Restful-Booker API Documentation](https://restful-booker.herokuapp.com/apidoc/index.html)
- [Rest Assured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/)

## 📈 Performance Notes
- The retry system adds minimal overhead:
- Only activates when tests fail
- Exponential backoff prevents API overload
- Smart filtering avoids unnecessary retries
- Configurable settings for different environments
